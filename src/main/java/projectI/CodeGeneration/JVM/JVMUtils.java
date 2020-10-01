package projectI.CodeGeneration.JVM;

import org.objectweb.asm.MethodVisitor;
import projectI.AST.Declarations.PrimitiveType;
import projectI.AST.Types.RuntimePrimitiveType;
import projectI.AST.Types.RuntimeType;

import static org.objectweb.asm.Opcodes.D2I;
import static org.objectweb.asm.Opcodes.I2D;

public class JVMUtils {
    public static void generateCastIfNecessary(MethodVisitor methodVisitor, RuntimeType from, RuntimeType to) {
        if (from.equals(to)) return;
        if (!(from instanceof RuntimePrimitiveType)) return;
        if (!(to instanceof RuntimePrimitiveType)) return;

        var fromPrimitiveType = ((RuntimePrimitiveType) from).type;
        if (fromPrimitiveType == PrimitiveType.BOOLEAN)
            fromPrimitiveType = PrimitiveType.INTEGER;

        var toPrimitiveType = ((RuntimePrimitiveType) to).type;
        if (toPrimitiveType == PrimitiveType.BOOLEAN)
            toPrimitiveType = PrimitiveType.INTEGER;

        if (fromPrimitiveType == PrimitiveType.INTEGER && toPrimitiveType == PrimitiveType.REAL)
            methodVisitor.visitInsn(I2D);
        else if (fromPrimitiveType == PrimitiveType.REAL && toPrimitiveType == PrimitiveType.INTEGER)
            methodVisitor.visitInsn(D2I);
    }
}
