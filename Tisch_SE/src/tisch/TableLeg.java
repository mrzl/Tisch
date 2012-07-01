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

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;

/**
 * TableLeg.java
 * 
 * @author marcels
 * 
 *         this class contains the mesh for a tableleg. its realized by using
 *         two vectors points on the tabletop and the tablebottom. the
 *         connection between those two vectors is a cylinder - the tableleg.
 * 
 */
public class TableLeg {

	private WETriangleMesh tableLegMesh;

	/**
	 * initializes a tableleg by the given paramrters
	 * 
	 * @param bottom
	 *            a Vec3D vector pointing to where the leg should start on the
	 *            tablebottom
	 * @param top
	 *            a Vec3D vector pointing to where the leg should end on the
	 *            tabletop
	 * @param radius
	 *            radius/thickness of a tableleg
	 * @param resolution
	 *            the polygon resolution of the tableleg
	 */
	public TableLeg(Vec3D bottom, Vec3D top, float radius, int resolution) {

		tableLegMesh = new WETriangleMesh();

		float rotationAngle = PApplet.radians(360) / (float) resolution;

		for (int i = 0; i < resolution; i++) {

			// bottom
			tableLegMesh.addFace(new Vec3D(radius
					* PApplet.sin(rotationAngle * i), 0, radius
					* PApplet.cos(rotationAngle * i)).add(bottom), new Vec3D(0,
					0, 0).add(bottom), new Vec3D(radius
					* PApplet.sin(rotationAngle * (i + 1)), 0, radius
					* PApplet.cos(rotationAngle * (i + 1))).add(bottom));
			// top
			tableLegMesh.addFace(new Vec3D(radius
					* PApplet.sin(rotationAngle * i), 0, radius
					* PApplet.cos(rotationAngle * i)).add(top), new Vec3D(0, 0,
					0).add(top), new Vec3D(radius
					* PApplet.sin(rotationAngle * (i + 1)), 0, radius
					* PApplet.cos(rotationAngle * (i + 1))).add(top));
			// sides
			tableLegMesh.addFace(new Vec3D(radius
					* PApplet.sin(rotationAngle * i), 0, radius
					* PApplet.cos(rotationAngle * i)).add(bottom), new Vec3D(
					radius * PApplet.sin(rotationAngle * i), 0, radius
							* PApplet.cos(rotationAngle * i)).add(top),
					new Vec3D(radius * PApplet.sin(rotationAngle * (i + 1)), 0,
							radius * PApplet.cos(rotationAngle * (i + 1)))
							.add(bottom));
			tableLegMesh.addFace(new Vec3D(radius
					* PApplet.sin(rotationAngle * (i + 1)), 0, radius
					* PApplet.cos(rotationAngle * (i + 1))).add(bottom),
					new Vec3D(radius * PApplet.sin(rotationAngle * (i + 1)), 0,
							radius * PApplet.cos(rotationAngle * (i + 1)))
							.add(top), new Vec3D(radius
							* PApplet.sin(rotationAngle * i), 0, radius
							* PApplet.cos(rotationAngle * i)).add(top));
		}
	}

	/**
	 * getter of the mesh
	 * 
	 * @return tableLegMesh returns the mesh of one tableleg
	 */
	public WETriangleMesh getMesh() {
		return this.tableLegMesh;
	}

}
