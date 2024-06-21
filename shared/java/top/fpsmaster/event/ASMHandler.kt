package top.fpsmaster.event

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import top.fpsmaster.utils.java.EventClassLoader
import java.lang.reflect.Method

object ASMHandler {
    private var index = 0
    fun loadHandlerClass(listener: Any, method: Method): Handler {
        val declaringClass = method.declaringClass
        val className = "EventHandler$index"
        val cv = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)

        cv.visit(V1_8, ACC_PUBLIC or ACC_SUPER, className, null, Type.getInternalName(Handler::class.java), null)
        cv.visitSource("$className.java", null)

        // Original code: super(listener, method)
        var mv: MethodVisitor = cv.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;Ljava/lang/reflect/Method;)V", null, null)
        mv.visitCode()
        mv.visitVarInsn(ALOAD, 0)
        mv.visitVarInsn(ALOAD, 1)
        mv.visitVarInsn(ALOAD, 2)
        mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Handler::class.java), "<init>", "(Ljava/lang/Object;Ljava/lang/reflect/Method;)V", false)
        mv.visitInsn(RETURN)
        mv.visitMaxs(3, 3)
        mv.visitEnd()

        // Original code: this.getMethod().invoke(this.getListener(), event)
        mv = cv.visitMethod(ACC_PUBLIC, "invoke", "(L${Type.getInternalName(Event::class.java)};)V", null, null)
        mv.visitCode()
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "getMethod", "()Ljava/lang/reflect/Method;", false)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "getListener", "()Ljava/lang/Object;", false)
        mv.visitInsn(ICONST_1)
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object")
        mv.visitInsn(DUP)
        mv.visitInsn(ICONST_0)
        mv.visitVarInsn(ALOAD, 1)
        mv.visitInsn(AASTORE)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false)
        mv.visitInsn(POP)
        mv.visitInsn(RETURN)
        mv.visitMaxs(6, 3)
        mv.visitEnd()

        // Original code: this.getListener().getClass().getSimpleName() + " -> " + this.getMethod().getName()
        mv = cv.visitMethod(ACC_PUBLIC, "getLog", "()Ljava/lang/String;", null, null)
        mv.visitCode()
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder")
        mv.visitInsn(DUP)
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "getListener", "()Ljava/lang/Object;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
        mv.visitLdcInsn(" -> ")
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "getMethod", "()Ljava/lang/reflect/Method;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
        mv.visitInsn(ARETURN)
        mv.visitMaxs(5, 1)
        mv.visitEnd()

        cv.visitEnd()

        return EventClassLoader.defineClass(declaringClass.classLoader, className, cv.toByteArray()).getConstructor(Any::class.java, Method::class.java).newInstance(listener, method) as Handler
    }
}
