package edu.umd.cs.pugh.visitclass;
import java.io.PrintStream;
import org.apache.bcel.classfile.*;


/**
 * Fixedup of from org.apache.bcel.classfile.Visitor
 *
 * @version 980818
 * @author  <A HREF="http://www.cs.umd.edu/~pugh">William Pugh</A>
 */
public abstract class BetterVisitor implements Visitor {

   public ConstantPool constant_pool;
   protected String className = "none";
   protected String betterClassName = "none";
   protected String packageName = "none";
   protected String sourceFile = "none";
   protected JavaClass thisClass;
   protected String methodSig = "none";
   protected String betterMethodSig = "none";
   protected String methodName = "none";
   protected String betterMethodName = "none";
   protected String betterFieldName = "none";
   protected String fieldName = "none";
   protected String fieldSig = "none";
   protected String betterFieldSig = "none";
   protected boolean fieldIsStatic;
   protected String superclassName = "none";
   protected String betterSuperclassName = "none";

   protected String getStringFromIndex(int i) {
        ConstantUtf8 name = (ConstantUtf8)constant_pool.getConstant(i);
        return name.getBytes().intern();
        }

    protected int asUnsignedByte(byte b) {
        return 0xff & b;
        }

  // Accessors
  public String getBetterClassName() { return betterClassName; }
  public String getPackageName() { return packageName; }
  public String getSourceFile() { return sourceFile; }
  public String getBetterMethodName() { return betterMethodName; }
  public String getSuperclassName() { return superclassName; }
  public String getBetterSuperclassName() { return betterSuperclassName; }
  public String getFieldName() { return fieldName; }
  public String getFieldSig() { return fieldSig; }
  public boolean getFieldIsStatic() { return fieldIsStatic; }
  public String getMethodName() { return methodName; }
  public String getMethodSig() { return methodSig; }

  ////////////////// In short form //////////////////////
   // General classes
   public void visit(JavaClass obj) {}

   public void visit(ConstantPool obj) {}

   public void visit(Field obj) {}
   public void visit(Method obj) { }

   // Constants

   public void visit(Constant obj) {}
   public void visit(ConstantCP obj) { visit((Constant)obj); }
     public void visit(ConstantMethodref obj) 
			{ visit((ConstantCP)obj); }
     public void visit(ConstantFieldref obj) 
			{ visit((ConstantCP)obj); }
     public void visit(ConstantInterfaceMethodref obj) 
			{ visit((ConstantCP)obj); }
   public void visit(ConstantClass obj) { visit((Constant)obj); }
   public void visit(ConstantDouble obj) { visit((Constant)obj); }
   public void visit(ConstantFloat obj) { visit((Constant)obj); }
   public void visit(ConstantInteger obj) { visit((Constant)obj); }
   public void visit(ConstantLong obj) { visit((Constant)obj); }
   public void visit(ConstantNameAndType obj) { visit((Constant)obj); }
   public void visit(ConstantString obj) { visit((Constant)obj); }
   public void visit(ConstantUtf8 obj) { visit((Constant)obj); }

   // Attributes
   public void visit(Attribute obj) {}
     public void visit(Code obj) { visit((Attribute) obj); }
     public void visit(ConstantValue obj) { visit((Attribute) obj); }
     public void visit(ExceptionTable obj) { visit((Attribute) obj); }
     public void visit(InnerClasses obj) { visit((Attribute) obj); }
     public void visit(LineNumberTable obj) { visit((Attribute) obj); }
     public void visit(LocalVariableTable obj) { visit((Attribute) obj); }
     public void visit(SourceFile obj) { visit((Attribute) obj); }
     public void visit(Synthetic obj) { visit((Attribute) obj); }
     public void visit(Deprecated obj) { visit((Attribute) obj); }
     public void visit(Unknown obj) { visit((Attribute) obj); }

   // Extra classes (i.e. leaves in this context)
   public void visit(InnerClass obj) {}
   public void visit(LocalVariable obj) {}
   public void visit(LineNumber obj) {}
   public void visit(CodeException obj) {}
   public void visit(StackMapEntry obj) {}

  // Attributes
  public void visitCode(Code obj) 
		{ visit(obj); }
  public void visitCodeException(CodeException obj)    
		{ visit(obj); }
  // Constants
  public void visitConstantClass(ConstantClass obj)    
		{ visit(obj); }
  public void visitConstantDouble(ConstantDouble obj)    
		{ visit(obj); }
  public void visitConstantFieldref(ConstantFieldref obj)    
		{ visit(obj); }
  public void visitConstantFloat(ConstantFloat obj)    
		{ visit(obj); }
  public void visitConstantInteger(ConstantInteger obj)    
		{ visit(obj); }
  public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj)
		{ visit(obj); }
  public void visitConstantLong(ConstantLong obj)    
		{ visit(obj); }
  public void visitConstantMethodref(ConstantMethodref obj)    
		{ visit(obj); }
  public void visitConstantNameAndType(ConstantNameAndType obj)    
		{ visit(obj); }
  public void visitConstantPool(ConstantPool obj)    
		{ visit(obj); }
  public void visitConstantString(ConstantString obj)    
		{ visit(obj); }
  public void visitConstantUtf8(ConstantUtf8 obj)    
		{ visit(obj); }
  public void visitConstantValue(ConstantValue obj)    
		{ visit(obj); }
  public void visitDeprecated(Deprecated obj)    
		{ visit(obj); }
  public void visitExceptionTable(ExceptionTable obj)    
		{ visit(obj); }
  public void visitField(Field obj)     {
	        fieldName = getStringFromIndex(obj.getNameIndex());
	        fieldSig = getStringFromIndex(obj.getSignatureIndex());
		betterFieldSig = fieldSig.replace('/','.');
		betterFieldName = betterClassName + "." + fieldName
				+ " : " + betterFieldSig;
		fieldIsStatic = obj.isStatic();
		visit(obj); 
		}
  // Extra classes (i.e. leaves in this context)
  public void visitInnerClass(InnerClass obj)    
		{ visit(obj); }
  public void visitInnerClasses(InnerClasses obj)    
		{ visit(obj); }
  // General classes
  public void visitJavaClass(JavaClass obj)     {
	constant_pool = obj.getConstantPool();
        thisClass = obj;
	ConstantClass c = (ConstantClass)constant_pool.getConstant(obj.getClassNameIndex());
        className = getStringFromIndex(c.getNameIndex());
	betterClassName = className.replace('/','.');
	packageName = obj.getPackageName();
	sourceFile = obj.getSourceFileName();
        superclassName = obj.getSuperclassName();
	betterSuperclassName = superclassName.replace('/','.');
	visit(obj); 
	}
  public void visitLineNumber(LineNumber obj)    
		{ visit(obj); }
  public void visitLineNumberTable(LineNumberTable obj)    
		{ visit(obj); }
  public void visitLocalVariable(LocalVariable obj)    
		{ visit(obj); }
  public void visitLocalVariableTable(LocalVariableTable obj)    
		{ visit(obj); }
  public void visitMethod(Method obj)    {
	        methodName = getStringFromIndex(obj.getNameIndex());
	        methodSig = getStringFromIndex(obj.getSignatureIndex());
		betterMethodSig = methodSig.replace('/','.');
		betterMethodName = betterClassName + "." + methodName
				+ " : " + betterMethodSig;
		visit(obj); 
		}
  public void visitSourceFile(SourceFile obj)    
		{ visit(obj); }
  public void visitSynthetic(Synthetic obj)    
		{ visit(obj); }
  public void visitUnknown(Unknown obj)    
		{ visit(obj); }
   public void visitStackMapEntry(StackMapEntry obj)
		{ visit(obj); }
   public void visitStackMap(StackMap obj)
		{ visit(obj); }
   public void report(PrintStream out) {}
}
