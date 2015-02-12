package gov.nasa.jpf.observability.struct;

import java.util.HashMap;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;

public class CreateStructs {
	
	public static void main(String[] args) {
		
		Map<Integer, IfJVMAttributes> allAttrs = 
				new HashMap<Integer, IfJVMAttributes>();
		
		   //10:	ifeq	75
		ConditionLocation loc00 = new ConditionLocation("MotivatingExample.java", 4, 1, 1);
		IfJVMAttributes attr00 = new IfJVMAttributes(loc00, Boolean.TRUE);
		HashSet<VarRef> vr00 = new HashSet<VarRef>();
		vr00.add(new LocalVarRef(1));
		attr00.preprocess(CompletionValue.IF_FALSE, vr00, new BitSet(), new BitSet());
		allAttrs.put(10, attr00);
		
		   //14:	ifeq	75
		ConditionLocation loc01 = new ConditionLocation("MotivatingExample.java", 4, 1, 2);
		IfJVMAttributes attr01 = new IfJVMAttributes(loc01, Boolean.TRUE);
		allAttrs.put(14, attr01);

		HashSet<VarRef> vr01 = new HashSet<VarRef>();
		BitSet bs01 = new BitSet(); 
		vr01.add(new LocalVarRef(2));
		bs01.set(1);
		attr01.preprocess(CompletionValue.IF_EITHER, vr01, bs01, new BitSet());

		
		   //19:	ifeq	30
		ConditionLocation loc02 = new ConditionLocation("MotivatingExample.java", 5, 1, 1);
		IfJVMAttributes attr02 = new IfJVMAttributes(loc02, Boolean.TRUE);
		allAttrs.put(19, attr02);

		HashSet<VarRef> vr02 = new HashSet<VarRef>();
		vr02.add(new LocalVarRef(4));
		attr02.preprocess(CompletionValue.IF_FALSE, vr02, new BitSet(), new BitSet());

		   //23:	ifle	30
		ConditionLocation loc03 = new ConditionLocation("MotivatingExample.java", 5, 1, 2);
		IfJVMAttributes attr03 = new IfJVMAttributes(loc03, Boolean.TRUE);
		allAttrs.put(23, attr03);

		HashSet<VarRef> vr03 = new HashSet<VarRef>();
		BitSet bs03 = new BitSet(); 
		vr03.add(new LocalVarRef(3));
		bs03.set(1);
		attr03.preprocess(CompletionValue.IF_EITHER, vr03, bs03, new BitSet());		

		   //35:	ifne	48
		ConditionLocation loc04 = new ConditionLocation("MotivatingExample.java", 6, 1, 1);
		IfJVMAttributes attr04 = new IfJVMAttributes(loc04, Boolean.FALSE);
		allAttrs.put(35, attr04);

		HashSet<VarRef> vr04 = new HashSet<VarRef>();
		vr04.add(new LocalVarRef(5));
		attr04.preprocess(CompletionValue.IF_TRUE, vr04, new BitSet(), new BitSet());

		   //41:	if_icmpgt	48
		ConditionLocation loc05 = new ConditionLocation("MotivatingExample.java", 6, 1, 2);
		IfJVMAttributes attr05 = new IfJVMAttributes(loc05, Boolean.TRUE);
		allAttrs.put(41, attr05);

		HashSet<VarRef> vr05 = new HashSet<VarRef>();
		BitSet bs05 = new BitSet(); 
		vr05.add(new LocalVarRef(3));
		bs05.set(1);
		attr05.preprocess(CompletionValue.IF_EITHER, vr05, new BitSet(), bs05);

		   //54:	if_icmpge	75
		ConditionLocation loc06 = new ConditionLocation("MotivatingExample.java", 7, 1, 1);
		IfJVMAttributes attr06 = new IfJVMAttributes(loc06, Boolean.TRUE);
		allAttrs.put(54, attr06);

		HashSet<VarRef> vr06 = new HashSet<VarRef>();
		vr06.add(new LocalVarRef(3));
		attr06.preprocess(CompletionValue.IF_EITHER, vr06, new BitSet(), new BitSet());

		   //59:	ifne	72
		ConditionLocation loc07 = new ConditionLocation("MotivatingExample.java", 8, 1, 1);
		IfJVMAttributes attr07 = new IfJVMAttributes(loc07, Boolean.FALSE);
		allAttrs.put(59, attr07);

		HashSet<VarRef> vr07 = new HashSet<VarRef>();
		vr07.add(new LocalVarRef(6));
		attr07.preprocess(CompletionValue.IF_TRUE, vr07, new BitSet(), new BitSet());

		   //65:	if_icmplt	72
		ConditionLocation loc08 = new ConditionLocation("MotivatingExample.java", 8, 1, 2);
		IfJVMAttributes attr08 = new IfJVMAttributes(loc08, Boolean.TRUE);
		allAttrs.put(65, attr08);

		HashSet<VarRef> vr08 = new HashSet<VarRef>();
		BitSet bs08 = new BitSet(); 
		vr08.add(new LocalVarRef(3));
		bs08.set(1);
		attr08.preprocess(CompletionValue.IF_EITHER, vr08, new BitSet(), bs08);

		   //77:	ifeq	89
		ConditionLocation loc09 = new ConditionLocation("MotivatingExample.java", 11, 1, 1);
		IfJVMAttributes attr09 = new IfJVMAttributes(loc09, Boolean.TRUE);
		allAttrs.put(77, attr09);

		HashSet<VarRef> vr09 = new HashSet<VarRef>();
		vr09.add(new LocalVarRef(8));
		attr09.preprocess(CompletionValue.IF_FALSE, vr09, new BitSet(), new BitSet());

		   //82:	ifeq	89
		ConditionLocation loc10 = new ConditionLocation("MotivatingExample.java", 11, 1, 2);
		IfJVMAttributes attr10 = new IfJVMAttributes(loc10, Boolean.TRUE);
		allAttrs.put(82, attr10);

		HashSet<VarRef> vr10 = new HashSet<VarRef>();
		BitSet bs10 = new BitSet(); 
		vr10.add(new LocalVarRef(7));
		bs10.set(1);
		attr10.preprocess(CompletionValue.IF_EITHER, vr10, bs10, new BitSet());

		   //94:	ifne	106
		ConditionLocation loc11 = new ConditionLocation("MotivatingExample.java", 12, 1, 1);
		IfJVMAttributes attr11 = new IfJVMAttributes(loc11, Boolean.FALSE);
		allAttrs.put(94, attr11);

		HashSet<VarRef> vr11 = new HashSet<VarRef>();
		vr11.add(new LocalVarRef(4));
		attr11.preprocess(CompletionValue.IF_TRUE, vr11, new BitSet(), new BitSet());

		   //99:	ifne	106
		ConditionLocation loc12 = new ConditionLocation("MotivatingExample.java", 12, 1, 2);
		IfJVMAttributes attr12 = new IfJVMAttributes(loc12, Boolean.FALSE);
		allAttrs.put(99, attr12);

		HashSet<VarRef> vr12 = new HashSet<VarRef>();
		BitSet bs12 = new BitSet(); 
		vr10.add(new LocalVarRef(5));
		bs10.set(1);
		attr10.preprocess(CompletionValue.IF_EITHER, vr12, bs12, new BitSet());

		   //111:	ifeq	118
		ConditionLocation loc13 = new ConditionLocation("MotivatingExample.java", 13, 1, 1);
		IfJVMAttributes attr13 = new IfJVMAttributes(loc13, Boolean.TRUE);
		allAttrs.put(111, attr13);

		HashSet<VarRef> vr13 = new HashSet<VarRef>();
		vr13.add(new LocalVarRef(6));
		attr13.preprocess(CompletionValue.IF_EITHER, vr13, new BitSet(), new BitSet());

		
	}
	
}