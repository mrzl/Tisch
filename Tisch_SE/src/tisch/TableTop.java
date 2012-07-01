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

import processing.core.*;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

/**
 * TableTop.java
 * 
 * @author marcels
 * 
 *         s
 * 
 */
public class TableTop {

	private PApplet parentApplet;
	private ToxiclibsSupport toxiSupport;
	private WETriangleMesh tableMesh;

	private Vec3D tableCenter;
	private float tableRadius;
	private Color tableColor;

	private boolean isSmooth = true;
	private float circleAngle;

	public TableTop(PApplet parent, Vec3D center, float radius,
			Color fillColor, int pointCount, float thickness, boolean pyr,
			boolean upwards, float bottomConeHeight, float topConeHeight) {

		parentApplet = parent;
		toxiSupport = new ToxiclibsSupport(parentApplet);
		tableMesh = new WETriangleMesh();

		this.tableCenter = center;
		this.tableRadius = radius;
		this.tableColor = fillColor;

		circleAngle = PApplet.radians(360) / (float) pointCount;

		for (int i = 0; i < pointCount; i++) {
			// basic ground circle vertices
			if (pyr && !upwards) {
				tableMesh.addFace(new Vec3D(radius
						* PApplet.sin(circleAngle * i), 0, radius
						* PApplet.cos(circleAngle * i)).add(center), new Vec3D(
						0, -topConeHeight, 0).add(center), new Vec3D(radius
						* PApplet.sin(circleAngle * (i + 1)), 0, radius
						* PApplet.cos(circleAngle * (i + 1))).add(center));
			} else {
				tableMesh.addFace(new Vec3D(radius
						* PApplet.sin(circleAngle * i), 0, radius
						* PApplet.cos(circleAngle * i)).add(center), new Vec3D(
						0, 0, 0).add(center), new Vec3D(radius
						* PApplet.sin(circleAngle * (i + 1)), 0, radius
						* PApplet.cos(circleAngle * (i + 1))).add(center));
			}
			// thickness vertices
			tableMesh.addFace(new Vec3D(radius * PApplet.sin(circleAngle * i),
					0, radius * PApplet.cos(circleAngle * i)).add(center),
					new Vec3D(radius * PApplet.sin(circleAngle * i), thickness,
							radius * PApplet.cos(circleAngle * i)).add(center),
					new Vec3D(radius * PApplet.sin(circleAngle * (i + 1)), 0,
							radius * PApplet.cos(circleAngle * (i + 1)))
							.add(center));
			tableMesh.addFace(new Vec3D(radius
					* PApplet.sin(circleAngle * (i + 1)), 0, radius
					* PApplet.cos(circleAngle * (i + 1))).add(center),
					new Vec3D(radius * PApplet.sin(circleAngle * (i + 1)),
							thickness, radius
									* PApplet.cos(circleAngle * (i + 1)))
							.add(center), new Vec3D(radius
							* PApplet.sin(circleAngle * i), thickness, radius
							* PApplet.cos(circleAngle * i)).add(center));
			// circle vertices on top of thickness layer
			if (pyr && upwards) {
				tableMesh.addFace(
						new Vec3D(radius * PApplet.sin(circleAngle * i),
								thickness, radius
										* PApplet.cos(circleAngle * i))
								.add(center),
						// add something to the sickess of second vertex in
						// order to
						// make it triangular
						new Vec3D(0, bottomConeHeight, 0).add(center),
						new Vec3D(radius * PApplet.sin(circleAngle * (i + 1)),
								thickness, radius
										* PApplet.cos(circleAngle * (i + 1)))
								.add(center));
			} else {
				tableMesh.addFace(new Vec3D(radius
						* PApplet.sin(circleAngle * i), thickness, radius
						* PApplet.cos(circleAngle * i)).add(center),
				// add something to the sickess of second vertex in
						// order to
						// make it triangular
						new Vec3D(0, thickness, 0).add(center), new Vec3D(
								radius * PApplet.sin(circleAngle * (i + 1)),
								thickness, radius
										* PApplet.cos(circleAngle * (i + 1)))
								.add(center));
			}
		}
	}

	public void drawTableTop() {
		tableMesh.computeFaceNormals();

		if (isSmooth)
			tableMesh.computeVertexNormals();

		parentApplet.fill(tableColor.getRed(), tableColor.getGreen(),
				tableColor.getBlue(), tableColor.getAlpha());
		parentApplet.noStroke();
		// wireframe
		// p.stroke(255, 255, 0);
		// gfx.meshNormalMapped(mesh, isSmooth, 0);
		toxiSupport.mesh(tableMesh, true, 0);

	}

	WETriangleMesh getMesh() {
		return tableMesh;
	}

	public Vec3D getCenter() {
		return tableCenter;
	}

	public void setCenter(Vec3D center) {
		this.tableCenter = center;
	}

	public float getRadius() {
		return tableRadius;
	}

	public void setRadius(float radius) {
		this.tableRadius = radius;
	}

	public Color getFillColor() {
		return tableColor;
	}

	public void setFillColor(Color fillColor) {
		this.tableColor = fillColor;
	}

	public boolean isSmooth() {
		return isSmooth;
	}

	public void setSmooth(boolean isSmooth) {
		this.isSmooth = isSmooth;
	}

	public float getAngle() {
		return circleAngle;
	}

	public void setAngle(float angle) {
		this.circleAngle = angle;
	}

}
