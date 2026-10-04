package mchorse.mappet.mixins;

import java.nio.ByteBuffer;
import net.minecraft.class_287;
import net.minecraft.class_293;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(class_287.class)
public interface BufferBuilderAccessor {
   @Accessor("field_1555")
   ByteBuffer mappet$buffer();

   /** Offset of the element (plus the base of the vertex) currently being written, in bytes */
   @Accessor("field_20884")
   int mappet$offset();

   @Accessor("field_1565")
   class_293 mappet$format();
}