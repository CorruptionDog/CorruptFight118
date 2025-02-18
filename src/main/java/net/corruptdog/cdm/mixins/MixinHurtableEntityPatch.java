//package net.corruptdog.cdm.mixins;
//
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.phys.Vec3;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Overwrite;
//import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
//import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;
//
//@Mixin(HurtableEntityPatch.class)
//public abstract class MixinHurtableEntityPatch <T extends LivingEntity> extends EntityPatch<T> {
//
//    /**
//     * @author
//     */
//    @Overwrite
//    public void knockBackEntity(Vec3 sourceLocation, float power) {
//        double d1 = sourceLocation.x() - this.original.getX();
//
//        double d0;
//        for(d0 = sourceLocation.z() - this.original.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
//            d1 = (Math.random() - Math.random()) * 0.01D;
//        }
//
//        power = (float)((double)power * (1.0D - this.original.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
//        if ((double)power > 0.0D) {
//            this.original.hasImpulse = true;
//            this.original.hurtMarked = true;
//            Vec3 vec3 = this.original.getDeltaMovement();
//            Vec3 vec31 = (new Vec3(d1, 0.0D, d0)).normalize().scale(power);
//            this.original.setDeltaMovement(vec3.x / 2.0D - vec31.x, this.original.isOnGround() ? Math.min(0.4D, vec3.y / 2.0D) : vec3.y, vec3.z / 2.0D - vec31.z);
//        }
//    }
//}