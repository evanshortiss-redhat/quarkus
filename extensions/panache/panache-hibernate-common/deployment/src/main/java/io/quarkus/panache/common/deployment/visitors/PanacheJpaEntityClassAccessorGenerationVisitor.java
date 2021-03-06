package io.quarkus.panache.common.deployment.visitors;

import org.hibernate.bytecode.enhance.spi.EnhancerConstants;
import org.jboss.jandex.ClassInfo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import io.quarkus.panache.common.deployment.EntityField;
import io.quarkus.panache.common.deployment.EntityModel;

public class PanacheJpaEntityClassAccessorGenerationVisitor extends PanacheEntityClassAccessorGenerationVisitor {

    public PanacheJpaEntityClassAccessorGenerationVisitor(ClassVisitor outputClassVisitor,
            ClassInfo entityInfo, EntityModel entityModel) {
        super(outputClassVisitor, entityInfo, entityModel);
    }

    @Override
    protected void generateAccessorSetField(MethodVisitor mv, EntityField field) {
        // Due to https://github.com/quarkusio/quarkus/issues/1376 we generate Hibernate read/write calls
        // directly rather than rely on Hibernate to see our generated accessor because it does not
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                thisClass.getInternalName(),
                EnhancerConstants.PERSISTENT_FIELD_WRITER_PREFIX + field.name,
                Type.getMethodDescriptor(Type.getType(void.class), Type.getType(field.descriptor)),
                false);
        // instead of:
        // mv.visitFieldInsn(Opcodes.PUTFIELD, thisClass.getInternalName(), field.name, field.descriptor);
    }

    @Override
    protected void generateAccessorGetField(MethodVisitor mv, EntityField field) {
        // Due to https://github.com/quarkusio/quarkus/issues/1376 we generate Hibernate read/write calls
        // directly rather than rely on Hibernate to see our generated accessor because it does not
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                thisClass.getInternalName(),
                EnhancerConstants.PERSISTENT_FIELD_READER_PREFIX + field.name,
                Type.getMethodDescriptor(Type.getType(field.descriptor)),
                false);
        // instead of:
        // mv.visitFieldInsn(Opcodes.GETFIELD, thisClass.getInternalName(), field.name, field.descriptor);
    }
}
