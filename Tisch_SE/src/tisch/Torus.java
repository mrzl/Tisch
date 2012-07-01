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
import processing.core.PConstants;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;

/**
 * Torus.java
 * 
 * @author marcels
 * 
 *         this class contains the mesh for a torus
 * 
 */
public class Torus {
	private float outerRadius;
	private float innerRad;
	private int resInner;
	private int resOuter;
	private int axis;
	private float paddingY;
	public WETriangleMesh mesh;

	public Torus(float outerRadius, float innerRad, int resInner, int resOuter,
			int axis, float paddingY) {
		this.outerRadius = outerRadius;
		this.innerRad = innerRad;
		this.resInner = resInner;
		this.resOuter = resOuter;
		this.axis = axis;
		this.paddingY = paddingY;
		mesh = new WETriangleMesh();
		createTorus(outerRadius, innerRad, resInner, resOuter, axis, paddingY);
	}

	public void createTorus(float outerRad, float innerRad, int numc, int numt,
			int axis, float paddingY) {
		float x, y, z, s, t, u, v;
		float aInner, aOuter;
		Vec3D[] tmp = new Vec3D[3];
		int counter = 0;
		int genCounter = 0;

		for (int i = 0; i < numc; i++) {
			for (int j = 0; j <= numt; j++) {
				t = j;
				v = t / (float) numt;
				aOuter = v * PConstants.TWO_PI;
				float cOut = PApplet.cos(aOuter);
				float sOut = PApplet.sin(aOuter);
				for (int k = 1; k >= 0; k--) {
					s = (i + k);
					u = s / (float) numc;
					aInner = u * PConstants.TWO_PI;
					float cIn = PApplet.cos(aInner);
					float sIn = PApplet.sin(aInner);

					if (axis == 0) {
						x = (outerRad + innerRad * cIn) * cOut;
						y = (outerRad + innerRad * cIn) * sOut;
						z = innerRad * sIn;
					} else if (axis == 1) {
						x = innerRad * sIn;
						y = (outerRad + innerRad * cIn) * sOut;
						z = (outerRad + innerRad * cIn) * cOut;
					} else {
						x = (outerRad + innerRad * cIn) * cOut;
						y = innerRad * sIn;
						z = (outerRad + innerRad * cIn) * sOut;
					}

					tmp[counter] = new Vec3D(x, y + paddingY, z);
					counter++;
					genCounter++;
					if (counter >= 3) {
						counter = 0;

					}
					if (genCounter > 2) {
						mesh.addFace(tmp[0], tmp[1], tmp[2]);
					}

				}
			}
		}
	}

	public WETriangleMesh getMesh() {
		return this.mesh;
	}

	public float getOuterRadius() {
		return outerRadius;
	}

	public void setOuterRadius(float outerRadius) {
		this.outerRadius = outerRadius;
		mesh.clear();
		createTorus(this.outerRadius, this.innerRad, this.resInner,
				this.resOuter, this.axis, this.paddingY);
	}

	public float getInnerRad() {
		return innerRad;
	}

	public void setInnerRad(float innerRad) {
		this.innerRad = innerRad;
		mesh.clear();
		createTorus(this.outerRadius, this.innerRad, this.resInner,
				this.resOuter, this.axis, this.paddingY);
	}

	public int getResInner() {
		return resInner;
	}

	public void setResInner(int resInner) {
		this.resInner = resInner;
		mesh.clear();
		createTorus(this.outerRadius, this.innerRad, this.resInner,
				this.resOuter, this.axis, this.paddingY);
	}

	public int getResOuter() {
		return resOuter;
	}

	public void setResOuter(int resOuter) {
		this.resOuter = resOuter;
		mesh.clear();
		createTorus(this.outerRadius, this.innerRad, this.resInner,
				this.resOuter, this.axis, this.paddingY);
	}

	public int getAxis() {
		return axis;
	}

	public void setAxis(int axis) {
		this.axis = axis;
		mesh.clear();
		createTorus(this.outerRadius, this.innerRad, this.resInner,
				this.resOuter, this.axis, this.paddingY);
	}

}
