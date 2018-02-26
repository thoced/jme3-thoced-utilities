package com.jme3.thoced;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.clone.JmeCloneable;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.control.EditableControl;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class MovableRigidBodyControl extends RigidBodyControl implements JmeCloneable,EditableControl {

    private Vector3f velocity = new Vector3f(0,0,0);

    private Vector3f startPosition;

    private float speed = 1f;

    private float distance = 0f;

    private Vector3f localEndPosition;

    private final String NAME_NODE_DESTINATION = "end_position";


    public MovableRigidBodyControl() {
    }

    public MovableRigidBodyControl(float mass) {
        super(mass);
    }

    public MovableRigidBodyControl(CollisionShape shape) {
        super(shape);
    }

    public MovableRigidBodyControl(CollisionShape shape, float mass) {
        super(shape, mass);


    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        startPosition = spatial.getLocalTranslation().clone();
        // reception du node de destination
        for(Spatial sp : ((Node)spatial).getChildren()){
            if(sp.getName().equals(NAME_NODE_DESTINATION)){
                    if(sp.getUserData("speed") != null)
                        speed = (float)sp.getUserData("speed");
                localEndPosition = sp.getLocalTranslation().clone();
                distance = localEndPosition.length();
                velocity = localEndPosition.normalize().mult(speed);
            }
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if(this.isEnabled()) {
            this.spatial.setLocalTranslation(this.spatial.getLocalTranslation().add(velocity.mult(tpf)));
            if((this.spatial.getLocalTranslation().subtract(startPosition)).length() >= distance){
                velocity.negateLocal();
                startPosition = this.spatial.getLocalTranslation().clone();
            }

        }

    }


    @Override
    public void setPhysicsSpace(PhysicsSpace space) {
        super.setPhysicsSpace(space);
    }

    @Override
    public Object jmeClone() {
        MovableRigidBodyControl move = new MovableRigidBodyControl(this.getCollisionShape(), this.getMass());
        move.setSpatial(spatial);
        move.setKinematic(this.isKinematic());
        move.setKinematicSpatial(this.isKinematicSpatial());
        move.setApplyPhysicsLocal(isApplyPhysicsLocal());
        move.setFriction(this.getFriction());
        move.setRestitution(this.getRestitution());
        move.setEnabled(this.isEnabled());
        move.setSpeed(this.getSpeed());
        return move;


    }


    @Override
    public @NotNull String getName() {
        return "Test";
    }

    @Override
    public @NotNull List<EditableProperty<?, ?>> getEditableProperties() {
        return null;
    }
}
