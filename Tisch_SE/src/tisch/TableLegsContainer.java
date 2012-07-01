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

import processing.core.*;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

/**
 * TableLegsContainer.java
 * 
 * This class contains multiple legs.
 */
public class TableLegsContainer {
	private PApplet parentApplet;
	private WETriangleMesh mesh;
	private ToxiclibsSupport gfx;

	private int tableLegCount;
	private TableLeg[] tib;
	private Vec3D tableLegLocOnTableTop[];
	private Vec3D tableLegLocOnGround[];

	/**
	 * the constructor initializes multiple tablelegs by the given parameters
	 * 
	 * @param parent
	 *            PApplet the processing applet. needed for drawing
	 * @param tischBeinCount
	 *            the number of tablelegs
	 * @param centerGround
	 *            the location of the center of the tablebottom
	 * @param radiusGround
	 *            the radius of the tableground
	 * @param centerTable
	 *            the location of the center of the tabletop
	 * @param radiusTable
	 *            the radius of the tabletop
	 * @param beinThickness
	 *            the thickness of the tablelegs
	 */
	TableLegsContainer(PApplet parent, int tischBeinCount, Vec3D centerGround,
			float radiusGround, Vec3D centerTable, float radiusTable,
			float beinThickness) {
		this.parentApplet = parent;
		this.tableLegCount = tischBeinCount;

		mesh = new WETriangleMesh();
		gfx = new ToxiclibsSupport(parentApplet);

		tableLegLocOnTableTop = new Vec3D[tischBeinCount];
		tableLegLocOnGround = new Vec3D[tischBeinCount];
		tib = new TableLeg[tischBeinCount];
		for (int i = 0; i < tischBeinCount; i++) {
			this.tableLegLocOnTableTop[i] = locOnTableTop(centerTable,
					radiusTable);
			this.tableLegLocOnGround[i] = locOnGround(centerGround,
					radiusGround, i);
			tib[i] = new TableLeg(tableLegLocOnGround[i],
					tableLegLocOnTableTop[i], beinThickness, 30);

			mesh.addMesh(tib[i].getMesh());
		}
		mesh.computeFaceNormals();
		mesh.computeVertexNormals();
	}

	/**
	 * draws the mesh, which contains all tablelegs with the ToxiclibsSupport
	 * class.
	 */
	public void drawTischbeine() {
		parentApplet.noStroke();
		gfx.mesh(mesh, false, 0);
	}

	/**
	 * getter for the mesh of all tablelegs
	 * 
	 * @return mesh mesh containing all tablelegs.
	 */
	public WETriangleMesh getMesh() {
		return this.mesh;
	}

	/**
	 * calculating a random location on the ring of the tabletop
	 * 
	 * @param centerTable
	 *            center of the table
	 * @param radiusTable
	 *            radius of the table
	 * @return Vec3D containing a generated location
	 */
	private Vec3D locOnTableTop(Vec3D centerTable, float radiusTable) {
		float tempRad = parentApplet.random(radiusTable * .7f, radiusTable);
		Vec3D ret = new Vec3D(tempRad
				* PApplet.sin(parentApplet.random(PApplet.TWO_PI)), 0, tempRad
				* PApplet.cos(parentApplet.random(PApplet.TWO_PI)));
		if (PApplet.dist(ret.x, ret.y, ret.z, 0, 0, 0) > radiusTable) {
			return locOnTableTop(centerTable, radiusTable);
		} else {
			return new Vec3D(ret).add(centerTable);
		}
	}

	/**
	 * calculating a random location on the ring of the tablebottom
	 * 
	 * @param centerTable
	 *            center of the tablebottom
	 * @param radiusTable
	 *            radius of the tablebottom
	 * @return Vec3D containing a generated location
	 */
	private Vec3D locOnGround(Vec3D centerGround, float radiusGround, int i) {
		return new Vec3D(radiusGround * 0.95f
				* PApplet.sin(PApplet.TWO_PI / tableLegCount * i), 0,
				radiusGround * 0.95f
						* PApplet.cos(PApplet.TWO_PI / tableLegCount * i))
				.add(centerGround);
	}

}
