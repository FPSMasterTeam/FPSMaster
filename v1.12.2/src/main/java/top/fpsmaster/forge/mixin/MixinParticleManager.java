package top.fpsmaster.forge.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEmitter;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.fpsmaster.features.impl.optimizes.Performance;

import java.util.List;
import java.util.Queue;

@Mixin(ParticleManager.class)
public class MixinParticleManager {

    @Final
    @Shadow
    private Queue<ParticleEmitter> particleEmitters = Queues.newArrayDeque();

    /**
     * @author SuperSkidder
     * @reason particles limit
     */
    @Overwrite
    public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes) {
        if (!Performance.using || particleEmitters.size() < Performance.particlesLimit.getValue().intValue()) {
            this.particleEmitters.add(new ParticleEmitter(Minecraft.getMinecraft().world, entityIn, particleTypes));
        }
    }

    /**
     * @author SuperSkidder
     * @reason particles limit
     */
    @Overwrite
    public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes, int p_191271_3_) {
        if (!Performance.using || particleEmitters.size() < Performance.particlesLimit.getValue().intValue()) {
            this.particleEmitters.add(new ParticleEmitter(Minecraft.getMinecraft().world, entityIn, particleTypes,p_191271_3_));
        }
    }
}
