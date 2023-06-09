/*
 * $RCSfile$
 *
 * Copyright 1997-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 * $Revision: 127 $
 * $Date: 2008-02-28 21:18:51 +0100 (Thu, 28 Feb 2008) $
 * $State$
 */

package projekt.math;

/**
 * A 3-element vector that is represented by single-precision floating point
 * x,y,z coordinates.  If this value represents a normal, then it should
 * be normalized.
 *
 */
public class Vec3f extends Tup3f implements java.io.Serializable {

    // Combatible with 1.1
    static final long serialVersionUID = -7031930069184524614L;

    /**
     * Constructs and initializes a Vec3f from the specified xyz coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public Vec3f(float x, float y, float z)
    {
        super(x,y,z);
    }


    /**
     * Constructs and initializes a Vec3f from the array of length 3.
     * @param v the array of length 3 containing xyz in order
     */
    public Vec3f(float[] v)
    {
        super(v);
    }


    /**
     * Constructs and initializes a Vec3f from the specified Vec3f.
     * @param v1 the Vec3f containing the initialization x y z data
     */
    public Vec3f(Vec3f v1)
    {
        super(v1);
    }


    /**
     * Constructs and initializes a Vec3f from the specified Vector3d.
     * @param v1 the Vector3d containing the initialization x y z data
     */

    /**
     * Constructs and initializes a Vec3f from the specified Tup3f.
     * @param t1 the Tup3f containing the initialization x y z data
     */
    public Vec3f(Tup3f t1) {
        super(t1);
    }


    public Vec3f()
    {
        super();
    }


    public Vec3f(float value){
        this.x = value;
        this.y = value;
        this.z = value;
    }
    /**
     * Returns the squared length of this vector.
     * @return the squared length of this vector
     */
    public final float lengthSquared()
    {
        return (this.x*this.x + this.y*this.y + this.z*this.z);
    }

    /**
     * Returns the length of this vector.
     * @return the length of this vector
     */
    public final float length()
    {
        return (float)
                Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
    }


    /**
     * Sets this vector to be the vector cross product of vectors v1 and v2.
     * @param v1 the first vector
     * @param v2 the second vector
     */
    public final void cross(Vec3f v1, Vec3f v2)
    {
        float x,y;

        x = v1.y*v2.z - v1.z*v2.y;
        y = v2.x*v1.z - v2.z*v1.x;
        this.z = v1.x*v2.y - v1.y*v2.x;
        this.x = x;
        this.y = y;
    }

    /**
     * Computes the dot product of this vector and vector v1.
     * @param v1 the other vector
     * @return the dot product of this vector and v1
     */
    public final float dot(Vec3f v1)
    {
        return (this.x*v1.x + this.y*v1.y + this.z*v1.z);
    }

    /**
     * Sets the value of this vector to the normalization of vector v1.
     * @param v1 the un-normalized vector
     */
    public final void normalize(Vec3f v1)
    {
        float norm;

        norm = (float) (1.0/Math.sqrt(v1.x*v1.x + v1.y*v1.y + v1.z*v1.z));
        this.x = v1.x*norm;
        this.y = v1.y*norm;
        this.z = v1.z*norm;
    }

    /**
     * Normalizes this vector in place.
     */
    public final void normalize()
    {
        float norm;

        norm = (float)
                (1.0/Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z));
        this.x *= norm;
        this.y *= norm;
        this.z *= norm;
    }


    /**
     *   Returns the angle in radians between this vector and the vector
     *   parameter; the return value is constrained to the range [0,PI].
     *   @param v1    the other vector
     *   @return   the angle in radians in the range [0,PI]
     */
    public final float angle(Vec3f v1)
    {
        double vDot = this.dot(v1) / ( this.length()*v1.length() );
        if( vDot < -1.0) vDot = -1.0;
        if( vDot >  1.0) vDot =  1.0;
        return((float) (Math.acos( vDot )));
    }


    public Vec3f mul(Vec3f v2){
        float resultX = x * v2.x;
        float resultY = y * v2.y;
        float resultZ = z * v2.z;
        return new Vec3f(resultX, resultY, resultZ);
    }

    public Vec3f mul(float value){
        float resultX = x * value;
        float resultY = y * value;
        float resultZ = z * value;
        return new Vec3f(resultX, resultY, resultZ);
    }


    public Vec3f divide(float value){
        float resultX = x / value;
        float resultY = y / value;
        float resultZ = z / value;
        return new Vec3f(resultX, resultY, resultZ);
    }



    public Vec3f sub(Vec3f v1, Vec3f v2){
        float resultX = v1.x - v2.x;
        float resultY = v1.y - v2.y;
        float resultZ = v1.z - v2.z;
        return new Vec3f(resultX, resultY, resultZ);
    }

    public Vec3f sub(Vec3f v){
        float resultX = x - v.x;
        float resultY = y - v.y;
        float resultZ = z - v.z;
        return new Vec3f(resultX, resultY, resultZ);
    }

    public Vec3f normalized(){
        float magnitude = this.length();
            float resultX  = x / magnitude;
            float resultY = y / magnitude;
            float resultZ = z / magnitude;
            return new Vec3f(resultX, resultY, resultZ);

    }

    public Vec3f max(Vec3f other) {
        float newX = Math.max(x, other.x);
        float newY = Math.max(y, other.y);
        float newZ = Math.max(z, other.z);
        return new Vec3f(newX, newY, newZ);
    }


}
