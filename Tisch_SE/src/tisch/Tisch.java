/**
 * Projekt: "Tisch"
 * Marcel Schwittlick s0529494
 * Prof. Dr. Elke Naumann
 * Software Engineering
 * SS2012
 * HTW Berlin
 * 
 * Es soll eine Lokale Anwendung entwickelt werden, die es ermöglicht Modelle
 * von einzigartigen Tischen zu generieren. Diese Modelle können in einem
 * weiteren Schritt ausgedruckt werden und als Tisch benutzt werden. Durch die
 * algorithmische Natur des Designs der Tische hängt ein Teil der Gestaltung vom
 * Zufall ab- was ein grundlegendes Element des Designprozesses sein soll. Das
 * Programm soll einen freien Designprozess von Tischen ermöglichen.
 *
 */

package tisch;

import java.awt.Color;
import java.io.File;
import java.util.Calendar;

import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlFont;
import controlP5.ControlP5;
import controlP5.ControlWindow;
import controlP5.RadioButton;
import controlP5.Slider;
import controlP5.Textlabel;
import controlP5.Toggle;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PFont;
import sunflow.SunflowAPIAPI;
import toxi.geom.Vec3D;
import toxi.geom.mesh.LaplacianSmooth;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;
import toxi.volume.HashIsoSurface;
import toxi.volume.IsoSurface;
import toxi.volume.MeshVoxelizer;
import toxi.volume.VolumetricSpace;

/*
 * Tisch.java
 * 
 *  Wenn das Programm von Github gecloned nicht funktioniert müssen folgende Schritte durchgeführt werden:
 *  1. Rechtsklick aufs Projekt -> Build Path
 *  2. jogl.jar auswählen und erweitern
 *  3. native library location ändern und im ordner /modes/java/libraries/opengl/library/ die passenden bibliotheken für jogl
 *  ausgewählt werden
 *  4. danach sollte das programm auf jeder plattform funktionieren.
 */
public class Tisch extends PApplet {

	private static final long serialVersionUID = 1L;

	// upper tabletop
	TableTop tableTop;
	float tableHeight = 150;
	Vec3D tableCenter = new Vec3D(0, tableHeight, 0); // to be interfaced
	float tableTopRadius = 130; // to be interfaced

	// bottom
	TableTop tableBottomNormal;
	Torus tableBottomTorus;
	Vec3D groundCenter = new Vec3D(0, 0, 0); // to be interfaced
	float tableGroundRadius = 80; // to be interfaced
	float tableTopThickness = 2.0f; // to be interfaced
	float tableGroundThickness = 2.0f;

	// tablelegs
	int tableLegCount = 10; // to be interfaced
	TableLegsContainer tableLegContainer;
	float tableLegThickness = 5.0f;

	boolean istopConed = false;
	boolean isBottomConed = false;
	boolean isBottomTorus = false;
	float topConeHeight = 30;
	float bottomConeHeight = 30;

	// SUNFLOW, PEASYCAM, TOXICLIBS, CONTROLp5
	SunflowAPIAPI sunflow;
	PeasyCam cam;
	ToxiclibsSupport gfx;

	ControlP5 controlP5;
	ControlWindow controlWindow;
	Slider sliders[] = new Slider[20];
	Button buttons[] = new Button[20];
	Textlabel textlabelGeneral;
	RadioButton radioButtonLighting;
	RadioButton radioButtonCamera;
	RadioButton radioButtonShader;
	float padding = 10.0f;

	// entire mesh
	WETriangleMesh mesh;
	boolean isWireframe = false;

	String timeStamp;

	int sceneWidth = 1280;
	int sceneHeight = 720;

	private boolean generated = false;
	private int meshVoxRes = 32;
	private int wallThickness = 1;
	private float surfaceIso = 0.2f;

	float[] lookAt;
	float[] pos;
	String selectedShader;

	public void setup() {
		size(sceneWidth, sceneHeight, OPENGL);
		hint(ENABLE_OPENGL_4X_SMOOTH);

		// SUNFLOW, PEASYCAM, TOXICLIBS, CONTROLp5

		sunflow = new SunflowAPIAPI();
		sunflow.setWidth(sceneWidth * 2);
		sunflow.setHeight(sceneHeight * 2);
		sunflow.setBackground(0, 0, 0);
		sunflow.setAaMax(1);
		sunflow.setAaMin(2);

		cam = new PeasyCam(this, 500);

		gfx = new ToxiclibsSupport(this);
		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(false);
		controlWindow = controlP5.addControlWindow("controlWindow", 1500, 100,
				650, 550);
		controlWindow.hideCoordinates();
		controlWindow.setTitle("control");

		initGui();
		timeStamp = timestamp();
	}

	public void draw() {
		background(50);
		frame.setTitle((int) frameRate + "fps");
		noStroke();
		lights();
		shininess(5.0f);
		controlP5.show();
		controlP5.hide();
		controlP5.draw();

		// draws mesh
		if (generated) {
			gfx.mesh(mesh);
		}
	}

	public void keyPressed() {
		if (key == ' ') {
			exit();
		} else if (key == 'v') {
			voxelizeMesh();
		} else if (key == 'p') {
			// to get this to work; close the mesh of the tabletop and the legs.
			mesh.rotateY(radians(3));
		} else if (key == 'l') {
			// smooth the mesh
			new LaplacianSmooth().filter(mesh, 1);

		}
	}

	private void generateTableLegs() {
		tableLegContainer = new TableLegsContainer(this, tableLegCount,
				groundCenter, tableGroundRadius, tableCenter, tableTopRadius,
				tableLegThickness);
	}

	/*
	 * interface the cone shape
	 */
	private void generateTableTop() {
		tableCenter.y = tableHeight;

		tableTop = new TableTop(this, tableCenter, tableTopRadius, new Color(
				30, 200, 200), 100, tableTopThickness, istopConed, false,
				bottomConeHeight, topConeHeight);
	}

	private void generateTableBottom() {

		if (!isBottomTorus) {
			tableBottomNormal = new TableTop(this, groundCenter,
					tableGroundRadius, new Color(200, 200, 30), 100,
					tableGroundThickness, isBottomConed, isBottomConed,
					bottomConeHeight, topConeHeight);
		} else {
			tableBottomTorus = new Torus(tableGroundRadius,
					tableGroundThickness * 2, 40, 40, 2, 0);
		}
	}

	private void initGui() {

		controlP5.addTextlabel("settingsLabel", "Settings", 10, 10).moveTo(
				controlWindow);
		controlP5.addTextlabel("generalLabel", "General", 10, 30).moveTo(
				controlWindow);
		controlP5.addTextlabel("specialsLabel", "Special", 10, 330).moveTo(
				controlWindow);
		controlP5.addTextlabel("editingLabel", "Editing", 360, 30).moveTo(
				controlWindow);
		controlP5.addTextlabel("finalizingLabel", "Finalizing", 360, 210)
				.moveTo(controlWindow);
		controlP5.addTextlabel("renderingLabel", "Rendering", 360, 280).moveTo(
				controlWindow);

		// initializing sliders
		int sliderPosition = 0;

		// tableheight
		sliders[sliderPosition] = controlP5.addSlider("tableHeight", 100, 300,
				tableHeight, 10, 50, 200, 20);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// number of legs
		sliders[sliderPosition] = controlP5.addSlider("tableLegCount", 1, 100,
				tableLegCount, 10, 90, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(100);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// thickness of legs
		sliders[sliderPosition] = controlP5.addSlider("tableLegThickness", 1,
				10, tableLegThickness, 10, 125, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(10);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// tabletop radius
		sliders[sliderPosition] = controlP5.addSlider("tableTopRadius", 100,
				200, tableTopRadius, 10, 170, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(100);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// tabletop thickness
		sliders[sliderPosition] = controlP5.addSlider("tableTopThickness", 1,
				10, tableTopThickness, 10, 205, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(10);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// tablebottom radius
		sliders[sliderPosition] = controlP5.addSlider("tableGroundRadius", 100,
				200, tableGroundRadius, 10, 250, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(100);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// tablebottom thickness
		sliders[sliderPosition] = controlP5.addSlider("tableGroundThickness",
				1, 10, tableGroundThickness, 10, 285, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(10);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// bottom cone height
		sliders[sliderPosition] = controlP5.addSlider("topConeHeight", 30, 100,
				topConeHeight, 10, 420, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(70);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// top cone height
		sliders[sliderPosition] = controlP5.addSlider("bottomConeHeight", 30,
				100, bottomConeHeight, 10, 455, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(70);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// meshvoxelize resolution
		sliders[sliderPosition] = controlP5.addSlider("meshVoxRes", 32, 128,
				meshVoxRes, 360, 50, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(94);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// wallthickness
		sliders[sliderPosition] = controlP5.addSlider("wallThickness", 0, 2,
				wallThickness, 360, 85, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(3);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// surface iso
		sliders[sliderPosition] = controlP5.addSlider("surfaceIso", 0.1f, 1.0f,
				surfaceIso, 360, 120, 200, 20);
		sliders[sliderPosition].setNumberOfTickMarks(10);
		sliders[sliderPosition].snapToTickMarks(true);
		sliders[sliderPosition].moveTo(controlWindow);
		sliderPosition++;

		// radiobuttons for sunflow lighting settings
		radioButtonLighting = controlP5.addRadioButton("radioButtonLighting",
				360, 300);
		radioButtonLighting.setColorForeground(color(120));
		radioButtonLighting.setColorActive(color(255));
		radioButtonLighting.setColorLabel(color(255));
		radioButtonLighting.setItemsPerRow(5);
		radioButtonLighting.setSpacingColumn(50);
		radioButtonLighting.moveTo(controlWindow);
		addToRadioButton(radioButtonLighting, "directionalLight", 1);
		addToRadioButton(radioButtonLighting, "pointLight", 2);
		addToRadioButton(radioButtonLighting, "sunSkyLight", 3);

		// radiobuttons for sunflow camera settings
		radioButtonCamera = controlP5.addRadioButton("radioButtonCamera", 360,
				330);
		radioButtonCamera.setColorForeground(color(120));
		radioButtonCamera.setColorActive(color(255));
		radioButtonCamera.setColorLabel(color(255));
		radioButtonCamera.setItemsPerRow(5);
		radioButtonCamera.setSpacingColumn(50);
		radioButtonCamera.moveTo(controlWindow);
		addToRadioButton(radioButtonCamera, "pinhole", 1);
		addToRadioButton(radioButtonCamera, "thinens", 2);
		addToRadioButton(radioButtonCamera, "fishye", 3);
		addToRadioButton(radioButtonCamera, "spherical", 4);

		// radiobuttons for sunflow shader settings
		radioButtonShader = controlP5.addRadioButton("radioButtonShader", 360,
				360);
		radioButtonShader.setColorForeground(color(120));
		radioButtonShader.setColorActive(color(255));
		radioButtonShader.setColorLabel(color(255));
		radioButtonShader.setItemsPerRow(4);
		radioButtonShader.setSpacingColumn(50);
		radioButtonShader.moveTo(controlWindow);
		addToRadioButton(radioButtonShader, "phong", 1);
		addToRadioButton(radioButtonShader, "diffuse", 2);
		addToRadioButton(radioButtonShader, "glass", 3);
		addToRadioButton(radioButtonShader, "mirror", 4);
		addToRadioButton(radioButtonShader, "ward", 5);
		addToRadioButton(radioButtonShader, "shinydiffuse", 6);

		controlP5.addToggle("topConed", false, 10, 350, 30, 30).moveTo(
				controlWindow);
		controlP5.addToggle("bottomConed", false, 130, 350, 30, 30).moveTo(
				controlWindow);
		controlP5.addToggle("bottomTorus", false, 250, 350, 30, 30).moveTo(
				controlWindow);

		// ---------- init button array ----------------
		int buttonPos = 0;

		buttons[buttonPos] = controlP5.addButton("generate", 0, 200, 500, 90,
				30);
		buttons[buttonPos].moveTo(controlWindow);
		buttonPos++;

		// smooth button
		buttons[buttonPos] = controlP5.addButton("smooth", 0, 360, 155, 60, 30);
		buttons[buttonPos].moveTo(controlWindow);
		buttonPos++;

		// savestlputton
		buttons[buttonPos] = controlP5
				.addButton("saveSTL", 0, 500, 230, 60, 30);
		buttons[buttonPos].moveTo(controlWindow);
		buttonPos++;

		// vxelizebutton
		buttons[buttonPos] = controlP5.addButton("voxelize", 0, 430, 155, 60,
				30);
		buttons[buttonPos].moveTo(controlWindow);
		buttonPos++;

		// flatten tabletop
		buttons[buttonPos] = controlP5.addButton("flattenTop", 0, 360, 230, 60,
				30);
		buttons[buttonPos].moveTo(controlWindow);
		buttonPos++;

		// flatten tablebottom
		buttons[buttonPos] = controlP5.addButton("flattenBottom", 0, 430, 230,
				60, 30);
		buttons[buttonPos].moveTo(controlWindow);
		buttonPos++;

		// sunflow render button
		buttons[buttonPos] = controlP5
				.addButton("render", 0, 360, 400, 100, 30);
		buttons[buttonPos].moveTo(controlWindow);
		buttonPos++;

		// deform mesh
		buttons[buttonPos] = controlP5.addButton("deform", 0, 500, 155, 60, 30);
		buttons[buttonPos].moveTo(controlWindow);
		buttonPos++;

	}

	public void addToRadioButton(RadioButton theRadioButton, String theName,
			int theValue) {
		Toggle t = theRadioButton.addItem(theName, theValue);
		t.captionLabel().setColorBackground(color(80));
		t.captionLabel().style().movePadding(2, 0, -1, 2);
		t.captionLabel().style().moveMargin(-2, 0, 0, -3);
		t.captionLabel().style().backgroundWidth = 46;
	}

	public void controlEvent(ControlEvent theEvent) {
		print("got an event from " + theEvent.group().name() + "\t");
		// lighting
		if (theEvent.group().name().equals("radioButtonLighting")) {
			switch ((int) theEvent.group().value()) {
			case 1:
				println("dirlight");
				sunflow.setDirectionalLight("dirlight", new Point3(-2, 3, 0),
						new Vector3(1, 1, 1), 1, new Color(0.4f, 0.4f, 0));
				// camera settings
				lookAt = cam.getLookAt();
				pos = cam.getPosition();
				sunflow.setCameraTarget(lookAt[0], lookAt[1], lookAt[2]);
				sunflow.setCameraPosition(pos[0], pos[1], pos[2]);
				break;
			case 2:
				println("pointlight");
				sunflow.setPointLight("pointLight", new Point3(500, 500, 500),
						new Color(255, 255, 255));
				// camera settings
				lookAt = cam.getLookAt();
				pos = cam.getPosition();
				sunflow.setCameraTarget(lookAt[0], lookAt[1], lookAt[2]);
				sunflow.setCameraPosition(pos[0], pos[1], pos[2]);
				break;
			case 3:
				println("sunskylight");
				sunflow.setSunSkyLight("sunskylight");
				// camera settings
				lookAt = cam.getLookAt();
				pos = cam.getPosition();
				sunflow.setCameraTarget(lookAt[0], lookAt[1], lookAt[2]);
				sunflow.setCameraPosition(pos[0], pos[1], pos[2]);
				break;
			}
		}
		// camera
		if (theEvent.group().name().equals("radioButtonCamera")) {
			switch ((int) theEvent.group().value()) {
			case 1:
				println("pinholecam");
				sunflow.setPinholeCamera("pinholeCam", 50f, (float) sceneWidth
						/ sceneHeight);
				// planes
				sunflow.drawPlane("ground", new Point3(0, 1500, 0),
						new Vector3(0, 1, 0));
				sunflow.drawPlane("sky", new Point3(0, -1600, 0), new Vector3(
						0, -1, 0));
				break;
			case 2:
				println("thinlenscam");
				sunflow.setThinlensCamera("thinLensCamera", 90f,
						(float) sceneWidth / sceneHeight);
				// planes
				sunflow.drawPlane("ground", new Point3(0, 1500, 0),
						new Vector3(0, 1, 0));
				sunflow.drawPlane("sky", new Point3(0, -1600, 0), new Vector3(
						0, -1, 0));
				break;
			case 3:
				println("fisheye");
				sunflow.setFisheyeCamera("fisheyeCam");
				// planes
				sunflow.drawPlane("ground", new Point3(0, 1500, 0),
						new Vector3(0, 1, 0));
				sunflow.drawPlane("sky", new Point3(0, -1600, 0), new Vector3(
						0, -1, 0));
				break;
			case 4:
				println("sphericalcam");
				sunflow.setSphericalCamera("sphericalCam");
				// planes
				sunflow.drawPlane("ground", new Point3(0, 1500, 0),
						new Vector3(0, 1, 0));
				sunflow.drawPlane("sky", new Point3(0, -1600, 0), new Vector3(
						0, -1, 0));
				break;
			}
		}
		// shader
		if (theEvent.group().name().equals("radioButtonShader")) {
			switch ((int) theEvent.group().value()) {
			case 1:
				println("phongshader");
				sunflow.removeShader(selectedShader);
				selectedShader = "phongShader";
				sunflow.setPhongShader(selectedShader, new Color(0.5f, 0.5f,
						0.8f), new Color(1.0f, 1.0f, 1.0f), 1, 8);

				break;
			case 2:
				println("diffuseshader");
				sunflow.removeShader(selectedShader);
				selectedShader = "diffuse";
				sunflow.setDiffuseShader(selectedShader, new Color(0.5f, 0.5f,
						0f));
				break;
			case 3:
				println("glassshader");
				sunflow.removeShader(selectedShader);
				selectedShader = "myGlassShader";
				sunflow.setGlassShader(selectedShader, new Color(1f, 1f, 1f),
						2.5f, 3f, new Color(1f, 1f, 1f));
				break;
			case 4:
				println("mirrorshader");
				sunflow.removeShader(selectedShader);
				selectedShader = "mirrorShader";
				sunflow.setMirrorShader(selectedShader, new Color(0.8f, 0.6f,
						0.2f));
				break;
			case 5:
				println("wardshader");
				sunflow.removeShader(selectedShader);
				selectedShader = "wardShader";
				sunflow.setWardShader(selectedShader, new Color(0.5f, 0.5f,
						0.8f), new Color(1.0f, 1.0f, 1.0f), 5, 5, 8);
				break;
			case 6:
				println("shinydiffuseshader");
				sunflow.removeShader(selectedShader);
				selectedShader = "myShinyShader";
				sunflow.setShinyDiffuseShader(selectedShader, new Color(0.6f,
						0.25f, 0.9f), 0.4f);
				break;
			}
		}
	}

	/*
	 * sunflow rendering
	 */
	public void renderScene() {

		lookAt = cam.getLookAt();
		pos = cam.getPosition();
		sunflow.setCameraTarget(lookAt[0], lookAt[1], lookAt[2]);
		sunflow.setCameraPosition(pos[0], pos[1], pos[2]);

		sunflow.drawMesh("myMesh", mesh.getUniqueVerticesAsArray(), mesh
				.getFacesAsArray());

		sunflow.setPathTracingGIEngine(64); // to be interfaced

		sunflow.render(true, sketchPath + "/" + timeStamp + "/" + timestamp()
				+ ".png");
	}

	public void generate(int theVal) {
		// init top, bottom, legs & gui
		generateTableBottom();
		generateTableTop();
		generateTableLegs();

		// mesh
		mesh = new WETriangleMesh().addMesh(tableLegContainer.getMesh())
				.addMesh(tableTop.getMesh());
		if (isBottomTorus) {
			mesh.addMesh(tableBottomTorus.getMesh());
		} else {
			mesh.addMesh(tableBottomNormal.getMesh());
		}

		// for realtime shading
		mesh.computeFaceNormals();
		mesh.computeVertexNormals();

		// bool to avoid weird exception
		generated = true;
	}

	public void saveSTL(int theVal) {

		new File(sketchPath + "/" + timeStamp).mkdir();
		mesh.saveAsSTL(sketchPath + "/" + timeStamp + "/" + timestamp()
				+ ".stl");
	}

	public void voxelize(int theVal) {
		generated = false;
		voxelizeMesh();
		generated = true;
	}

	public void deform() {
		deformMesh();
	}

	public void smooth() {
		new LaplacianSmooth().filter(mesh, 1);
	}

	void deformMesh() {
		for (Vec3D v : mesh.getVertices()) {
			if (random(1) < 0.2) {
				v.scaleSelf(random(0.8f, 1.2f));
			}
		}
		mesh.rebuildIndex();
	}

	public void topConed(boolean theFlag) {
		istopConed = !istopConed;
	}

	public void bottomConed(boolean theFlag) {
		isBottomConed = !isBottomConed;
	}

	public void bottomTorus(boolean theFlag) {
		isBottomTorus = !isBottomTorus;
	}

	public void flattenBottom() {
		generated = false;
		constrainTableBottom();
		generated = true;
	}

	public void flattenTop() {
		generated = false;
		constrainTableTop();
		generated = true;
	}

	public void render() {
		renderScene();
	}

	public void voxelizeMesh() {
		MeshVoxelizer voxelizer = new MeshVoxelizer(meshVoxRes);
		// try setting to 1 or 2 (voxels)
		voxelizer.setWallThickness(wallThickness);
		VolumetricSpace vol = voxelizer.voxelizeMesh(mesh);
		vol.closeSides();
		IsoSurface surface = new HashIsoSurface(vol);
		mesh = new WETriangleMesh();
		surface.computeSurfaceMesh(mesh, surfaceIso);
		mesh.faceOutwards();
		mesh.computeFaceNormals();
		mesh.computeVertexNormals();
	}

	private String timestamp() {
		Calendar now = Calendar.getInstance();
		return String.format("%1$ty%1$tm%1$td_%1$tH%1$tM%1$tS", now);
	}

	/*
	 * generates a flat surface as tabletop.
	 */
	public void constrainTableTop() {
		float maxY = 0;
		for (Vec3D v : mesh.getVertices()) {
			if (v.y > maxY) {
				maxY = v.y;
			}
		}
		println(maxY);
		for (Vec3D v : mesh.getVertices()) {
			if (v.y > maxY - maxY * 0.01) {
				v.y = maxY - maxY * 0.01f;
			}
		}
		mesh.rebuildIndex();
		mesh.computeFaceNormals();
		mesh.computeVertexNormals();
	}

	/*
	 * generates a flat surface as tablebottom.
	 */
	public void constrainTableBottom() {
		float minY = 100;
		for (Vec3D v : mesh.getVertices()) {
			if (v.y < minY) {
				minY = v.y;
			}
		}
		println(minY);
		for (Vec3D v : mesh.getVertices()) {
			if (v.y < minY + abs(minY * 0.01f)) {
				v.y = minY + abs(minY * 0.01f);
			}
		}
		mesh.rebuildIndex();
		mesh.computeFaceNormals();
		mesh.computeVertexNormals();
		mesh.faceOutwards();
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "tisch.Tisch" });
	}
}
