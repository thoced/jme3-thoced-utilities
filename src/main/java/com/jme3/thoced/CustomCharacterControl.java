package com.jme3.thoced;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.AbstractPhysicsControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import java.util.List;

//public  class CustomCharacterControl extends AbstractControl implements PhysicsControl,PhysicsTickListener{
public  class CustomCharacterControl extends AbstractPhysicsControl implements PhysicsTickListener{

    private PhysicsRigidBody rigidBodyControl;

    private PhysicsSpace physicsSpace;

    private boolean onGround = false;

    private boolean jump = false;

    private Vector3f direction = Vector3f.ZERO;

    private Vector3f frictionDirection = Vector3f.ZERO;

    private Vector3f viewDirection = Vector3f.UNIT_Z;

    private float height = 0f;

    private float radius = 0f;

    private float mass = 0f;

    private float speed = 4f;

    private Vector3f scale = new Vector3f(1,1,1);

    private Vector3f localUp = new Vector3f(0,1,0);

    private Spatial spatial = null;

    private Vector3f location = new Vector3f();

    private Vector3f velocity = new Vector3f(0,0,0);

    private Vector3f forward = new Vector3f(Vector3f.UNIT_Z);

    protected final Quaternion rotation = new Quaternion(Quaternion.DIRECTION_Z);


    public void setWalkDirection(Vector3f direction){
        this.direction = direction;
    }

    public void setViewDirection(Vector3f viewDirection){
            this.viewDirection = viewDirection;
    }

    public void applyDirectionToView(boolean apply){

    }

    public Vector3f getForward() {
        return forward;
    }


    public void setGravity(Vector3f gravity){
        rigidBodyControl.setGravity(gravity);
        localUp.set(gravity).normalizeLocal().negateLocal();
    }

    public void jump(){

        //rigidBodyControl.clearForces();
        jump = true;
    }

    public PhysicsRigidBody getRigidBodyControl() {
        return rigidBodyControl;
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }

    public Vector3f getWalkDirection() {
        return direction;
    }

    public Vector3f getFrictionDirection() {
        return frictionDirection;
    }

    public void setFrictionDirection(Vector3f frictionDirection) {
        this.frictionDirection = frictionDirection;
        this.frictionDirection.setY(0f);
    }

    @Override
    protected void createSpatialData(Spatial spat) {
        rigidBodyControl.setUserObject(spatial);
    }

    @Override
    protected void removeSpatialData(Spatial spat) {
        rigidBodyControl.setUserObject(null);
    }

    @Override
    protected void setPhysicsLocation(Vector3f vec) {
        rigidBodyControl.setPhysicsLocation(vec);
        location.set(vec);
    }

    @Override
    protected void setPhysicsRotation(Quaternion quat) {
        rotation.set(quat);
    }

    @Override
    protected void addPhysics(PhysicsSpace space) {
        space.addCollisionObject(rigidBodyControl);
        space.addTickListener(this);
    }

    @Override
    protected void removePhysics(PhysicsSpace space) {

    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        return null;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        this.spatial = spatial;


    }

       /* @Override
        protected void controlUpdate(float tpf) {


            direction.normalizeLocal();
            Vector3f currentVelocity = rigidBodyControl.getLinearVelocity();
            currentVelocity.setX(0f);
            currentVelocity.setZ(0f);
            currentVelocity.addLocal(direction.mult(speed));
            rigidBodyControl.setLinearVelocity(currentVelocity);



        }*/

        /*@Override
        protected void controlRender(RenderManager rm, ViewPort vp) {

        }*/

        public CustomCharacterControl(float radius, float height, float mass) {

        this.radius = radius;
        this.height = height;
        this.mass = mass;

        CapsuleCollisionShape capsuleCollisionShape = new CapsuleCollisionShape(radius,height);

        rigidBodyControl = new PhysicsRigidBody(capsuleCollisionShape,mass);
        rigidBodyControl.setAngularFactor(0f);

    }

    public CustomCharacterControl(CollisionShape collisionShape, float mass){
        this.mass = mass;
        rigidBodyControl = new PhysicsRigidBody(collisionShape,mass);
        //rigidBodyControl.setApplyPhysicsLocal(true);

        rigidBodyControl.setAngularFactor(0f);
        rigidBodyControl.setCcdMotionThreshold(0.1f);



    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        rigidBodyControl.getPhysicsLocation(location);
        //rotation has been set through viewDirection
        rigidBodyControl.getPhysicsRotation(rotation);
        applyPhysicsTransform(location, rotation);


        // forward du spatial
        if(direction.lengthSquared() != 0f) {
          //  Quaternion quat = rotation
            rotation.lookAt(direction, Vector3f.UNIT_Y);
            rigidBodyControl.setPhysicsRotation(rotation);
            forward = direction.clone();
        }

    }

    private void checkOnGround(){

            Vector3f startPoint = location;
            Vector3f endPoint = startPoint.subtract(Vector3f.UNIT_Y.mult(getFinalHeight()));
            List<PhysicsRayTestResult> results = this.getPhysicsSpace().rayTest(startPoint, endPoint);
            for (PhysicsRayTestResult physicsRayTestResult : results) {
                if (!physicsRayTestResult.getCollisionObject().equals(rigidBodyControl)) {
                    onGround = true;
                    return;

                }
            }
            onGround = false;


    }

    public boolean isOnGround() {
        return onGround;
    }

    protected float getFinalHeight() {

        if((this.radius * 2) > this.height ){
            return (this.radius * 2 + 0.1f) * scale.getY();
        }

        return (height + 0.1f) * scale.getY();
    }



    /*@Override
    public void setPhysicsSpace(PhysicsSpace space) {
        this.physicsSpace = space;
        rigidBodyControl.setPhysicsLocation(this.getSpatial().getLocalTranslation());
        this.physicsSpace.add(rigidBodyControl);



    }*/

    /*@Override
    public PhysicsSpace getPhysicsSpace() {
        return this.physicsSpace;
    }*/

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {

        // check on ground
        checkOnGround();

        if(!jump) {
            direction.normalizeLocal();
            Vector3f currentVelocity = velocity.clone();
            currentVelocity.setX(0f);
            currentVelocity.setZ(0f);
            currentVelocity.addLocal(direction.mult(speed));
            rigidBodyControl.setLinearVelocity(currentVelocity.add(frictionDirection));

        }
        else{
            rigidBodyControl.applyImpulse(new Vector3f(0,mass * 6,0),Vector3f.ZERO);
            jump = false;
        }


    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        rigidBodyControl.getLinearVelocity(velocity);
        frictionDirection.set(0,0,0);


    }

    @Override
    public Object jmeClone() {
        return null;
    }


}

