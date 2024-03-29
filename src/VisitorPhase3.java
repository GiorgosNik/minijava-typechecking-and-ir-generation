package src;

import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;

public class VisitorPhase3 extends GJDepthFirst<String, Object> {
    public LinkedHashMap<String, classMap> classes;
    public LinkedHashMap<String, String> variableRegister;
    public int registerCounter;
    public int labelCounter;
    public String lastAlloc;
    method methodObject = null;
    public String codeCollection = "";

    // This function will convert boolean literals or boolean expressions for
    // storage in the bool array
    public String convertBool(String givenEXPR) {
        if (givenEXPR.equals("true")) {
            return "" + 1;
        } else if (givenEXPR.equals("false")) {
            return "" + 0;
        } else {
            addLine("\t" + "%_" + registerCounter + " = zext i1 " + givenEXPR + " to i32");
            registerCounter++;
            return "%_" + (registerCounter - 1);
        }
    }

    // This function gets the type of the argument based on its index
    public String getMethodArgumentType(method thisMethod, int argument) {
        int index = 0;
        String parameterType = "";
        for (String parameter : thisMethod.formalParams.keySet()) {
            if (index == argument) {
                parameterType = thisMethod.formalParams.get(parameter).Type;
                if (parameterType == "int[]") {
                    parameterType = "i32*";
                } else if (parameterType == "int") {
                    parameterType = "i32";
                } else if (parameterType == "boolean") {
                    parameterType = "i1";
                } else if (classes.containsKey(parameterType)) {
                    parameterType = "i8*";
                }
                break;
            }
            index++;
        }

        return parameterType;
    }

    // Accumulate the code line by line
    public void addLine(String lineOfCode) {
        codeCollection += "\n" + lineOfCode;
    }

    // Return the method object, depending on the scope the call was made in
    public method getMethod(classMap objectClass, String calledMethod) {
        classMap thisClass = objectClass;
        List<classMap> parentList = new ArrayList<classMap>();
        LinkedHashMap<String, method> uniqueMethods;
        classMap parentClass = null;

        // Initialize the counters for the registers and labels;
        uniqueMethods = new LinkedHashMap<String, method>();

        // Methods defined in parent classes
        if (thisClass.parentClass != null) {
            parentClass = thisClass.parentClass;
        }
        while (parentClass != null) {
            parentList.add(parentClass);
            if (parentClass.parentClass != null) {
                parentClass = parentClass.parentClass;
            } else {
                parentClass = null;
            }
        }

        // Loop for parent classes
        for (int i = 0; i < parentList.size(); i++) {
            // Loop for methods of parent class
            for (String parentMethod : parentList.get(i).methods.keySet()) {
                if (uniqueMethods.containsKey(parentMethod)) {
                    uniqueMethods.replace(parentMethod, parentList.get(i).methods.get(parentMethod));
                } else {
                    uniqueMethods.put(parentMethod, parentList.get(i).methods.get(parentMethod));
                }
            }
        }

        for (String methodKey : thisClass.methods.keySet()) {
            if (uniqueMethods.containsKey(methodKey)) {
                uniqueMethods.replace(methodKey, thisClass.methods.get(methodKey));
            } else {
                uniqueMethods.put(methodKey, thisClass.methods.get(methodKey));
            }
        }
        for (String methodKey : uniqueMethods.keySet()) {
            if (methodKey == calledMethod) {
                methodObject = uniqueMethods.get(methodKey);
            }
        }

        return methodObject;
    }

    // Get the size of the V-Table of the class
    public int getVtableSize(classMap objectClass) {
        classMap thisClass = objectClass;
        List<classMap> parentList = new ArrayList<classMap>();
        LinkedHashMap<String, method> uniqueMethods;
        classMap parentClass = null;
        int index = 0;

        // Initialize the counters for the registers and labels;
        uniqueMethods = new LinkedHashMap<String, method>();

        // Methods defined in parent classes
        if (thisClass.parentClass != null) {
            parentClass = thisClass.parentClass;
        }
        while (parentClass != null) {
            parentList.add(parentClass);
            if (parentClass.parentClass != null) {
                parentClass = parentClass.parentClass;
            } else {
                parentClass = null;
            }
        }

        // Loop for parent classes
        for (int i = 0; i < parentList.size(); i++) {
            // Loop for methods of parent class
            for (String parentMethod : parentList.get(i).methods.keySet()) {
                if (uniqueMethods.containsKey(parentMethod)) {
                    uniqueMethods.replace(parentMethod, parentList.get(i).methods.get(parentMethod));
                } else {
                    uniqueMethods.put(parentMethod, parentList.get(i).methods.get(parentMethod));
                }
            }
        }

        for (String methodKey : thisClass.methods.keySet()) {
            if (uniqueMethods.containsKey(methodKey)) {
                uniqueMethods.replace(methodKey, thisClass.methods.get(methodKey));
            } else {
                uniqueMethods.put(methodKey, thisClass.methods.get(methodKey));
            }
        }

        return uniqueMethods.size();
    }

    // Get the offset of the method on the V-Table, depending on the scope the call
    // was made in
    public int getMethodOffset(classMap objectClass, String calledMethod) {
        classMap thisClass = objectClass;
        List<classMap> parentList = new ArrayList<classMap>();
        LinkedHashMap<String, method> uniqueMethods;
        classMap parentClass = null;
        int index = 0;

        // Initialize the counters for the registers and labels;
        uniqueMethods = new LinkedHashMap<String, method>();

        // Methods defined in parent classes
        if (thisClass.parentClass != null) {
            parentClass = thisClass.parentClass;
        }
        while (parentClass != null) {
            parentList.add(parentClass);
            if (parentClass.parentClass != null) {
                parentClass = parentClass.parentClass;
            } else {
                parentClass = null;
            }
        }

        // Loop for parent classes
        for (int i = 0; i < parentList.size(); i++) {
            // Loop for methods of parent class
            for (String parentMethod : parentList.get(i).methods.keySet()) {
                if (uniqueMethods.containsKey(parentMethod)) {
                    uniqueMethods.replace(parentMethod, parentList.get(i).methods.get(parentMethod));
                } else {
                    uniqueMethods.put(parentMethod, parentList.get(i).methods.get(parentMethod));
                }
            }
        }

        for (String methodKey : thisClass.methods.keySet()) {
            if (uniqueMethods.containsKey(methodKey)) {
                uniqueMethods.replace(methodKey, thisClass.methods.get(methodKey));
            } else {
                uniqueMethods.put(methodKey, thisClass.methods.get(methodKey));
            }
        }
        for (String methodKey : uniqueMethods.keySet()) {
            if (methodKey == calledMethod) {
                break;
            }
            index++;
        }

        return index;
    }

    // Returns the result of loading the given field or variable
    public String loadVarToRegister(String name, String Type, method currMethod) {
        classMap currClass = currMethod.belongsTo;
        String fieldType = null;
        int index = 0;
        int offset = 0;
        if (currMethod.definedVars.containsKey(name) || currMethod.formalParams.containsKey(name)) {
            if (Type == "int[]") {
                addLine("\t" + "%_" + registerCounter + " = load i32*, i32** %" + name);
            } else if (Type == "boolean[]") {
                addLine("\t" + "%_" + registerCounter + " = load i32*, i32** %" + name);
            } else if (Type == "int") {
                addLine("\t" + "%_" + registerCounter + " = load i32, i32* %" + name);
            } else if (Type == "boolean") {
                addLine("\t" + "%_" + registerCounter + " = load i1, i1* %" + name);
            } else if (classes.containsKey(Type)) {
                addLine("\t" + "%_" + registerCounter + " = load i8*, i8** %" + name);
            }
        } else {
            while (currClass != null) {
                if (currClass.fields.containsKey(name)) {
                    fieldType = currClass.fields.get(name).Type;
                    for (String fieldKey : currClass.fields.keySet()) {
                        if (fieldKey == name) {
                            offset = currClass.fieldOffset[index];
                            break;
                        }
                        index++;
                    }

                    break;
                } else {
                    currClass = currClass.parentClass;
                }
            }
            if (fieldType == "int") {
                addLine(
                        "\t" + "%_" + registerCounter + " =  getelementptr i8, i8* %this, i32 " + (offset + 8));
                registerCounter++;
                addLine(
                        "\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i32*");
                registerCounter++;
                addLine("\t" + "%_" + registerCounter + " = load i32, i32* %_" + (registerCounter - 1));

            } else if (fieldType == "int[]") {
                addLine(
                        "\t" + "%_" + registerCounter + " =  getelementptr i8, i8* %this, i32 " + (offset + 8));
                registerCounter++;
                addLine(
                        "\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i32**");
                registerCounter++;
                addLine("\t" + "%_" + registerCounter + " = load i32*, i32** %_" + (registerCounter - 1));
            } else if (fieldType == "boolean[]") {
                addLine(
                        "\t" + "%_" + registerCounter + " =  getelementptr i8, i8* %this, i32 " + (offset + 8));
                registerCounter++;
                addLine(
                        "\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i32**");
                registerCounter++;
                addLine("\t" + "%_" + registerCounter + " = load i32*, i32** %_" + (registerCounter - 1));
            } else if (fieldType == "boolean") {
                addLine(
                        "\t" + "%_" + registerCounter + " =  getelementptr i8, i8* %this, i32 " + (offset + 8));
                registerCounter++;
                addLine(
                        "\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i1*");
                registerCounter++;
                addLine("\t" + "%_" + registerCounter + " = load i1, i1* %_" + (registerCounter - 1));
            } else if (classes.containsKey(fieldType)) {
                addLine(
                        "\t" + "%_" + registerCounter + " =  getelementptr i8, i8* %this, i32 " + (offset + 8));
                registerCounter++;
                addLine(
                        "\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i8**");
                registerCounter++;
                addLine("\t" + "%_" + registerCounter + " = load i8*, i8** %_" + (registerCounter - 1));
            }
        }

        String result = " %_" + registerCounter;
        registerCounter++;
        return result;
    }

    // Stores the given expression to the given field or variable
    public String storeExpression(String name, String Type, method currMethod, String expr) {
        classMap currClass = currMethod.belongsTo;
        String fieldType = null;
        int index = 0;
        int offset = 0;
        if (currMethod.definedVars.containsKey(name) || currMethod.formalParams.containsKey(name)) {
            if (Type == "int[]") {
                addLine("\t" + "store i32* " + expr + ", i32** %" + name);
            } else if (Type == "boolean[]") {
                addLine("\t" + "store i32* " + expr + ", i32** %" + name);
            } else if (Type == "int") {
                addLine("\t" + "store i32 " + expr + ", i32* %" + name);
            } else if (Type == "boolean") {
                addLine("\t" + "store i1 " + expr + ", i1* %" + name);
            } else if (classes.containsKey(Type)) {
                addLine("\t" + "store i8* " + expr + ", i8** %" + name);
            }
        } else {
            while (currClass != null) {
                if (currClass.fields.containsKey(name)) {
                    fieldType = currClass.fields.get(name).Type;
                    for (String fieldKey : currClass.fields.keySet()) {
                        if (fieldKey == name) {
                            offset = currClass.fieldOffset[index];
                            break;
                        }
                        index++;
                    }
                    break;
                } else {
                    currClass = currClass.parentClass;
                }
            }
            if (fieldType == "int") {
                addLine(
                        "\t" + "%_" + registerCounter + " =  getelementptr i8, i8* %this, i32 " + (offset + 8));
                registerCounter++;
                addLine(
                        "\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i32*");
                addLine("\t" + "store i32 " + expr + ", i32* %_" + registerCounter);
            } else if (fieldType == "int[]") {
                addLine(
                        "\t" + "%_" + registerCounter + " =  getelementptr i8, i8* %this, i32 " + (offset + 8));
                registerCounter++;
                addLine(
                        "\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i32**");
                addLine("\t" + "store i32* " + expr + ", i32** %_" + registerCounter);
            } else if (fieldType == "boolean") {
                addLine(
                        "\t" + "%_" + registerCounter + " =  getelementptr i8, i8* %this, i32 " + (offset + 8));
                registerCounter++;
                addLine(
                        "\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i1*");
                addLine("\t" + "store i1 " + expr + ", i1* %_" + registerCounter);
            } else if (classes.containsKey(fieldType)) {
                addLine(
                        "\t" + "%_" + registerCounter + " =  getelementptr i8, i8* %this, i32 " + (offset + 8));
                registerCounter++;
                addLine(
                        "\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i8**");
                addLine("\t" + "store i8* " + expr + ", i8** %_" + registerCounter);
            }
        }

        registerCounter++;
        return null;
    }

    // Returns the type of the variable or field signified by name
    // Returns null if the name is not a field or variable
    public String isVar(String name, method currMethod) throws Exception {
        String type = null;
        if ((name.equals("int") || name.equals("boolean") || name.equals("int[]") || name.equals("boolean[]")
                || classes.containsKey(name))) {
            return name;
        }
        if (currMethod.formalParams.containsKey(name)) {
            type = currMethod.formalParams.get(name).Type;
        } else if (currMethod.definedVars.containsKey(name)) {
            type = currMethod.definedVars.get(name).Type;
        } else if (currMethod.belongsTo.fields.containsKey(name)) {
            type = currMethod.belongsTo.fields.get(name).Type;
        } else if (currMethod.belongsTo.parentClass != null) {
            classMap currClass = currMethod.belongsTo;
            while (currClass.parentClass != null) {
                if (currClass.parentClass.fields.containsKey(name)) {
                    type = currClass.parentClass.fields.get(name).Type;
                    break;
                } else {
                    currClass = currClass.parentClass;
                }
            }

        } else {
            return null;
        }
        return type;
    }

    public void passSymbolTable(LinkedHashMap<String, classMap> givenMap) {
        // Used to get the Symbol Table from Visitor #1
        classes = givenMap;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public String visit(MainClass n, Object argu) throws Exception {
        classMap thisClass = classes.get(n.f1.accept(this, argu));
        List<classMap> parentList = new ArrayList<classMap>();
        LinkedHashMap<String, method> uniqueMethods;
        classMap parentClass = null;
        String methodSubString;
        String methodString;
        lastAlloc = null;

        // Initialize the counters for the registers and labels;
        registerCounter = 0;
        labelCounter = 0;
        variableRegister = new LinkedHashMap<String, String>();

        // Loop for each class, create the V-Tables
        for (String key : classes.keySet()) {

            // Create the map to hold the inherited methods
            uniqueMethods = new LinkedHashMap<String, method>();

            // If class in not main
            if (key != thisClass.Name) {
                methodString = "";

                // Methods defined in parent classes
                if (classes.get(key).parentClass != null) {
                    parentClass = classes.get(key).parentClass;
                }
                while (parentClass != null) {
                    parentList.add(parentClass);
                    if (parentClass.parentClass != null) {
                        parentClass = parentClass.parentClass;
                    } else {
                        parentClass = null;
                    }
                }
                // Loop for parent classes
                for (int i = 0; i < parentList.size(); i++) {

                    // Loop for methods of parent class
                    for (String parentMethod : parentList.get(i).methods.keySet()) {
                        if (uniqueMethods.containsKey(parentMethod)) {
                            uniqueMethods.replace(parentMethod, parentList.get(i).methods.get(parentMethod));
                        } else {
                            uniqueMethods.put(parentMethod, parentList.get(i).methods.get(parentMethod));
                        }
                    }
                }
                for (String methodKey : classes.get(key).methods.keySet()) {
                    if (uniqueMethods.containsKey(methodKey)) {
                        uniqueMethods.replace(methodKey, classes.get(key).methods.get(methodKey));
                    } else {
                        uniqueMethods.put(methodKey, classes.get(key).methods.get(methodKey));
                    }
                }
                for (String methodKey : uniqueMethods.keySet()) {
                    if (!(methodKey.equals("main") && classes.get(key).parentClass != null
                            && classes.get(key).parentClass.methods.containsKey("main"))) {
                        methodSubString = "i8* bitcast (";
                        if (uniqueMethods.get(methodKey).Type == "int") {
                            methodSubString = methodSubString + "i32";
                        } else if (uniqueMethods.get(methodKey).Type == "boolean") {
                            methodSubString = methodSubString + "i1";
                        } else {
                            methodSubString = methodSubString + "i8*";
                        }
                        methodSubString = methodSubString + "(i8*";
                        for (String argumentKey : uniqueMethods.get(methodKey).formalParams.keySet()) {
                            if (uniqueMethods.get(methodKey).formalParams.get(argumentKey).Type == "int") {
                                methodSubString = methodSubString + ",i32";
                            } else if (uniqueMethods.get(methodKey).formalParams.get(argumentKey).Type == "boolean") {
                                methodSubString = methodSubString + ",i1";
                            } else {
                                methodSubString = methodSubString + ",i8*";
                            }
                        }
                        methodSubString = methodSubString + ")* @" + uniqueMethods.get(methodKey).belongsTo.Name + "."
                                + methodKey + " to i8*)";
                        if (methodString != "") {
                            methodString = methodString + ", \n" + methodSubString;
                        } else {
                            methodString = methodSubString;
                        }
                    } else {
                        methodSubString = "i8* bitcast (i32()* @main to i8*)";
                        if (methodString != "") {
                            methodString = methodString + ", \n" + methodSubString;
                        } else {
                            methodString = methodSubString;
                        }
                    }
                }

                addLine("@." + key + "_vtable = global ["
                        + String.valueOf(uniqueMethods.size()) + " x i8*][" + methodString + "]");

            }
        }
        // Define some usefull functions
        addLine("@." + thisClass.Name + "_vtable = global [0 x i8*] []");
        addLine("declare i8* @calloc(i32, i32)\ndeclare i32 @printf(i8*, ...)\ndeclare void @exit(i32)");
        addLine(
                "\n@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"");
        addLine(
                "\n@_cNSZ = constant [15 x i8] c\"Negative size\\0a\\00\"");
        addLine(
                "define void @print_int(i32 %i) {\n %_str = bitcast [4 x i8]* @_cint to i8*\n call i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n ret void\n}\n");
        addLine(
                "define void @throw_oob() {\n%_str = bitcast [15 x i8]* @_cOOB to i8*\ncall i32 (i8*, ...) @printf(i8* %_str)\ncall void @exit(i32 1)\nret void\n}\n");
        addLine(
                "define void @throw_nsz() {\n%_str = bitcast [15 x i8]* @_cNSZ to i8*\ncall i32 (i8*, ...) @printf(i8* %_str)\ncall void @exit(i32 1)\nret void\n}\n");
        addLine("define i32 @main() {");

        // Created all V-tables, now start working on the main function
        n.f14.accept(this, thisClass.methods.get("main"));
        n.f15.accept(this, thisClass.methods.get("main"));

        addLine("\t" + "ret i32 0");
        addLine("}\n");
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n, Object argu) throws Exception {
        String name = n.f1.accept(this, argu);
        classMap thisClass = classes.get(name);
        n.f4.accept(this, thisClass);
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n, Object argu) throws Exception {
        String name = n.f1.accept(this, argu);
        classMap thisClass = classes.get(name);
        n.f6.accept(this, thisClass);
        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public String visit(VarDeclaration n, Object argu) throws Exception {
        method thisMethod = (method) argu;
        String varName = n.f1.accept(this, null);
        // Get the type of the variable
        String type = thisMethod.definedVars.get(varName).Type;
        // Allocate space on the register of the variable
        String alloca = "%" + varName + " = alloca";

        // Combine the above
        if (type == "int") {
            alloca = alloca + " i32";
        } else if (type == "boolean") {
            alloca = alloca + " i1";
        } else if (type == "boolean[]") {
            alloca = alloca + " i32*";
        } else if (type == "int[]") {
            alloca = alloca + " i32*";
        } else {
            alloca = alloca + " i8*";
        }
        variableRegister.put(varName, "%" + varName);
        addLine("\t" + alloca);

        return null;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n, Object argu) throws Exception {
        classMap thisClass = (classMap) argu;

        // Get the type and name of the variable
        String type = n.f1.accept(this, argu);
        String name = n.f2.accept(this, argu);
        String llvmType;
        String defineString;
        String defineSubString;
        String parameterLoading = "";
        String returnString;
        if (type == "int") {
            llvmType = "i32";
        } else if (type == "boolean") {
            llvmType = "i1";
        } else if (type == "int[]") {
            llvmType = "i32*";
        } else if (type == "boolean[]") {
            llvmType = "i1*";
        } else {
            llvmType = "i8*";
        }

        // Define the method based on the name and type
        defineString = "define " + llvmType + " @" + thisClass.Name + "." + name + "(i8* %this";

        // Add the formal parameters to the definition
        for (String parameter : thisClass.methods.get(name).formalParams.keySet()) {
            parameterLoading += "%" + parameter;
            if (thisClass.methods.get(name).formalParams.get(parameter).Type == "int") {
                defineSubString = ", i32";
                parameterLoading += " = alloca i32\n";
                parameterLoading += "\tstore i32" + " %." + parameter + ", i32* %" + parameter + "\n";
            } else if (thisClass.methods.get(name).formalParams.get(parameter).Type == "boolean") {
                defineSubString = ", i1";
                parameterLoading += " = alloca i1\n";
                parameterLoading += "\tstore i1" + " %." + parameter + ", i1* %" + parameter + "\n";
            } else if (thisClass.methods.get(name).formalParams.get(parameter).Type == "int[]") {
                defineSubString = ", i32*";
                parameterLoading += " = alloca i32*\n";
                parameterLoading += "\tstore i32*" + " %." + parameter + ", i32** %" + parameter + "\n";
            } else if (thisClass.methods.get(name).formalParams.get(parameter).Type == "boolean[]") {
                defineSubString = ", i1*";
                parameterLoading += " = alloca i1*\n";
                parameterLoading += "\tstore i1*" + " %." + parameter + ", i1** %" + parameter + "\n";
            } else {
                defineSubString = ", i8*";
                parameterLoading += " = alloca i8*\n";
                parameterLoading += "\tstore i8*" + " %." + parameter + ", i8** %" + parameter + "\n";
            }
            defineSubString += " %." + parameter;
            defineString += defineSubString;

        }
        defineString += "){";
        addLine(defineString);
        if (!parameterLoading.equals("")) {
            addLine("\t" + parameterLoading);
        }

        // Work on the variables and statements
        n.f7.accept(this, thisClass.methods.get(name));
        n.f8.accept(this, thisClass.methods.get(name));

        // Prepare return type
        if (thisClass.methods.get(name).Type == "int") {
            defineSubString = "i32";
        } else if (thisClass.methods.get(name).Type == "boolean") {
            defineSubString = "i1";
        } else if (thisClass.methods.get(name).Type == "int[]") {
            defineSubString = "i32*";
        } else if (thisClass.methods.get(name).Type == "boolean[]") {
            defineSubString = "i1*";
        } else {
            defineSubString = "i8*";
        }
        returnString = n.f10.accept(this, thisClass.methods.get(name));
        if (isVar(returnString, thisClass.methods.get(name)) != null) {
            returnString = loadVarToRegister(returnString, thisClass.methods.get(name).Type,
                    thisClass.methods.get(name));
        }
        addLine("\n\tret " + defineSubString + " " + returnString);
        addLine("\t" + "}\n");
        return null;
    }

    /**
     * f0 -> "this"
     */
    @Override
    public String visit(ThisExpression n, Object argu) throws Exception {
        method thisMethod = (method) argu;
        lastAlloc = thisMethod.belongsTo.Name;
        return "%this";
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    @Override
    public String visit(ArrayAssignmentStatement n, Object argu) throws Exception {
        method thisMethod = (method) argu;
        String arrayName = n.f0.accept(this, argu);
        String arrayType = isVar(arrayName, (method) argu);
        String exprType;
        String arrayReg;
        arrayType = isVar(arrayType, (method) argu);
        if (arrayType != null) {
            arrayName = loadVarToRegister(arrayName, arrayType, thisMethod);
        }

        // Get the index and calculate the expression
        String arrayIndex = n.f2.accept(this, argu);
        String indexType = isVar(arrayIndex, (method) argu);
        if (indexType != null) {
            arrayIndex = loadVarToRegister(arrayIndex, indexType, thisMethod);
        }
        String exprReturn = n.f5.accept(this, argu);
        exprType = isVar(exprReturn, (method) argu);
        if (exprType != null) {
            exprReturn = loadVarToRegister(exprReturn, exprType, thisMethod);
        }

        addLine("\t" + "%_" + registerCounter + " = load i32, i32* " + arrayName);
        registerCounter++;
        addLine("\t" + "%_" + registerCounter + " = icmp sge i32 " + arrayIndex + ", 0");
        registerCounter++;
        addLine(
                "\t" + "%_" + registerCounter + " = icmp slt i32 " + arrayIndex + ", %_" + (registerCounter - 2));
        registerCounter++;
        addLine("\t" +
                "%_" + registerCounter + " = and i1 %_" + (registerCounter - 2) + ", %_" + (registerCounter - 1));
        registerCounter++;
        addLine(
                "\t" + "br i1 %_" + (registerCounter - 1) + ", label %oob_ok_" + labelCounter + ", label %oob_err_"
                        + labelCounter);
        addLine("\t" + "\noob_err_" + labelCounter + ":");
        addLine("\t" + "call void @throw_oob()");
        addLine("\t" + "br label %oob_ok_" + labelCounter);
        addLine("\t" + "\noob_ok_" + labelCounter + ":");

        addLine("\t" + "%_" + registerCounter + " = add i32 1, " + arrayIndex);
        registerCounter++;
        addLine("\t" + "%_" + registerCounter + " = getelementptr i32, i32* " + arrayName
                + ", i32 %_" + (registerCounter - 1));

        // Convert the i1 values to i32 if we store to a bool array
        if (arrayType == "boolean[]") {
            arrayReg = "%_" + registerCounter;
            registerCounter++;
            exprReturn = convertBool(exprReturn);

            addLine("\t" + "store i32 " + exprReturn + ", i32* " + arrayReg);
            labelCounter++;
        } else {
            registerCounter++;
            addLine("\t" + "store i32 " + exprReturn + ", i32* %_" + (registerCounter - 1));
            labelCounter++;
        }
        return null;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    @Override
    public String visit(IfStatement n, Object argu) throws Exception {
        String ifCondtion = n.f2.accept(this, argu);
        int ifLabel = labelCounter;
        method thisMethod = (method) argu;
        if (isVar(ifCondtion, (method) argu) != null) {
            ifCondtion = loadVarToRegister(ifCondtion, "boolean", thisMethod);
        }

        labelCounter++;
        addLine("\t" + "br i1 " + ifCondtion + ", label %if_then_" + ifLabel + ", label %if_else_" + ifLabel);

        addLine("\t" + "\nif_then_" + ifLabel + ":");
        n.f4.accept(this, argu);
        addLine("\t" + "br label %if_end_" + ifLabel);

        addLine("\t" + "\nif_else_" + ifLabel + ":");
        n.f6.accept(this, argu);
        addLine("\t" + "br label %if_end_" + ifLabel);

        addLine("\t" + "\nif_end_" + ifLabel + ":");
        return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    @Override
    public String visit(WhileStatement n, Object argu) throws Exception {
        int WhileExpressionLabel = labelCounter;
        labelCounter++;
        method thisMethod = (method) argu;
        // Jump to the start of the loop
        addLine("\t" + "br label %while_top_" + WhileExpressionLabel);
        addLine("\t" + "\nwhile_top_" + WhileExpressionLabel + ":");

        // Create condition expr and jump based on result
        String loopCondtion = n.f2.accept(this, argu);
        if (isVar(loopCondtion, (method) argu) != null) {
            loopCondtion = loadVarToRegister(loopCondtion, "boolean", thisMethod);
        }
        addLine(
                "\t" + "br i1 " + loopCondtion + ", label %while_in_" + WhileExpressionLabel + ", label %while_out_"
                        + WhileExpressionLabel);

        // In the loop
        addLine("\t" + "\nwhile_in_" + WhileExpressionLabel + ":");
        n.f4.accept(this, argu);
        addLine("\t" + "br label %while_top_" + WhileExpressionLabel);

        addLine("\t" + "\nwhile_out_" + WhileExpressionLabel + ":");
        return null;
    }

    /**
     * f0 -> "addLine"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    @Override
    public String visit(PrintStatement n, Object argu) throws Exception {
        String varName = n.f2.accept(this, argu);
        method thisMethod = (method) argu;
        if (isVar(varName, thisMethod) != null) {
            varName = loadVarToRegister(varName, isVar(varName, thisMethod), thisMethod);
        }
        addLine("\t" + "call void (i32) @print_int(i32 " + varName + ")");
        registerCounter++;
        return null;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    @Override
    public String visit(MessageSend n, Object argu) throws Exception {
        // Get the object the method is called on
        String className = n.f0.accept(this, argu);
        String classType;

        // Get the name of the method
        String methodName = n.f2.accept(this, argu);
        method thisMethod = (method) argu;
        method methodObject = null;
        String arguments = "";
        String type = "";

        // Get the type of the object the method is called on
        if (isVar(className, thisMethod) != null) {
            // First case: the object is a field or variable
            classType = isVar(className, thisMethod);
            className = loadVarToRegister(className, isVar(className, thisMethod), thisMethod);
        } else {
            // Second case: the object is a new [CLASS] expression, get the type that we
            // stored during allocation
            classType = lastAlloc;
        }

        // Use the argument list counter to calculate the index of each argument to
        // obtain the type
        argumentListType argumentCounter = new argumentListType(thisMethod,
                getMethod(classes.get(classType), methodName));
        String givenArguments = n.f4.accept(this, argumentCounter);
        if (givenArguments == null) {
            givenArguments = "";
        }

        addLine("\t" + "%_" + registerCounter + " = bitcast i8* " + className + " to i8***");
        registerCounter++;

        addLine("\t" + "%_" + registerCounter + " = load i8**, i8*** %_" + (registerCounter - 1));
        registerCounter++;

        addLine(
                "\t" + "%_" + registerCounter + " = getelementptr i8*, i8** %_" + (registerCounter - 1) + ", i32 "
                        + getMethodOffset(classes.get(classType), methodName));
        registerCounter++;

        addLine("\t" + "%_" + registerCounter + " = load i8*, i8** %_" + (registerCounter - 1));
        registerCounter++;

        methodObject = getMethod(classes.get(classType), methodName);
        if (methodObject.Type == "int[]") {
            type = "i32*";
        } else if (methodObject.Type == "int") {
            type = "i32";
        } else if (methodObject.Type == "boolean") {
            type = "i1";
        } else if (methodObject.Type == "boolean[]") {
            type = "i1*";
        } else if (classes.containsKey(methodObject.Type)) {
            type = "i8*";
        }
        for (String varKey : methodObject.formalParams.keySet()) {
            if (methodObject.formalParams.get(varKey).Type == "int[]") {
                arguments += " ,i32*";
            } else if (methodObject.formalParams.get(varKey).Type == "int") {
                arguments += " ,i32";
            } else if (methodObject.formalParams.get(varKey).Type == "boolean") {
                arguments += " ,i1";
            } else if (methodObject.formalParams.get(varKey).Type == "boolean[]") {
                arguments += " ,i1*";
            } else if (classes.containsKey(methodObject.formalParams.get(varKey).Type)) {
                arguments += " ,i8*";
            }
        }

        addLine("\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to " + type
                + " (i8*" + arguments + ")*");
        registerCounter++;

        addLine("\t" +
                "%_" + registerCounter + " = call " + type + " %_" + (registerCounter - 1) + " (i8* " + className + " "
                + givenArguments + ")");
        registerCounter++;
        lastAlloc = methodObject.Type;
        return "%_" + (registerCounter - 1);
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    @Override
    public String visit(ExpressionList n, Object argu) throws Exception {
        argumentListType argumentCounter = (argumentListType) argu;
        method thisMethod = argumentCounter.currMethod;
        String typeString = n.f0.accept(this, thisMethod);
        String type = isVar(typeString, thisMethod);
        String llvmType = "";
        // Add the first expression and its type to the accumulated string
        if (type != null) {
            if (type == "int[]") {
                llvmType = "i32*";
            } else if (type == "int") {
                llvmType = "i32";
            } else if (type == "boolean") {
                llvmType = "i1";
            } else if (type == "boolean[]") {
                llvmType = "i1*";
            } else if (classes.containsKey(type)) {
                llvmType = "i8*";
            }
            typeString = llvmType + " " + loadVarToRegister(typeString, type, thisMethod);
        } else {
            if (typeString == "true") {
                typeString = "i1 1";
            } else if (typeString == "false") {
                typeString = "i1 0";
            } else if (typeString.chars().allMatch(Character::isDigit)) {
                typeString = "i32 " + typeString;
            } else if (classes.containsKey(typeString)) {
                typeString = "i8* " + typeString;
            } else {
                typeString = getMethodArgumentType(argumentCounter.givenMethod, 0) + " " + typeString;
            }
        }
        // Do the same for the next arguments if any, passing the counter object
        argumentCounter.counter++;
        if (n.f1 != null) {
            typeString += n.f1.accept(this, argumentCounter);
        }
        return "," + typeString;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    @Override
    public String visit(ExpressionTail n, Object argu) throws Exception {
        argumentListType argumentCounter = (argumentListType) argu;
        String ret = "";
        for (Node node : n.f0.nodes) {
            ret += "," + node.accept(this, argumentCounter);
            argumentCounter.counter++;
        }
        return ret;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    @Override
    public String visit(ExpressionTerm n, Object argu) throws Exception {
        argumentListType argumentCounter = (argumentListType) argu;
        method thisMethod = argumentCounter.currMethod;
        String termResult = n.f1.accept(this, thisMethod);
        String type = isVar(termResult, thisMethod);
        String llvmType = "";
        // Calculate an expression of the expression list of arguments
        if (type != null) {
            if (type == "int[]") {
                llvmType = "i32*";
            } else if (type == "int") {
                llvmType = "i32";
            } else if (type == "boolean") {
                llvmType = "i1";
            } else if (type == "boolean[]") {
                llvmType = "i1*";
            } else if (classes.containsKey(type)) {
                llvmType = "i8*";
            }
            termResult = llvmType + " " + loadVarToRegister(termResult, type, thisMethod);
        } else {
            if (termResult == "true") {
                termResult = "i1 1";
            } else if (termResult == "false") {
                termResult = "i1 0";
            } else if (termResult.chars().allMatch(Character::isDigit)) {
                termResult = "i32 " + termResult;
            } else if (classes.containsKey(termResult)) {
                termResult = "i8* " + termResult;
            } else {
                termResult = getMethodArgumentType(argumentCounter.givenMethod, 0) + " " + termResult;
            }
        }
        return termResult;
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    @Override
    public String visit(AndExpression n, Object argu) throws Exception {
        String clause1 = n.f0.accept(this, argu);
        String secondClause = null;
        method thisMethod = (method) argu;
        int AndExpressionLabel = labelCounter;
        labelCounter++;
        if (isVar(clause1, (method) argu) != null) {
            clause1 = loadVarToRegister(clause1, "boolean", thisMethod);
        }
        addLine("\t" + "br i1 " + clause1 + ", label %exp_res_1_" + AndExpressionLabel + ", label %exp_res_0_"
                        + AndExpressionLabel);

        // exp_res_1
        addLine("\t" + "exp_res_0_" + AndExpressionLabel + ":");
        addLine("\t" + "br label %exp_res_3_" + AndExpressionLabel);

        // exp_res_1
        addLine("\t" + "exp_res_1_" + AndExpressionLabel + ":");
        String clause2 = n.f2.accept(this, argu);
        if (isVar(clause2, (method) argu) != null) {
            clause2 = loadVarToRegister(clause2, "boolean", thisMethod);
        }
        secondClause = clause2;
        addLine("\t" + "br label %exp_res_2_" + AndExpressionLabel);

        // exp_res_2
        addLine("\t" + "exp_res_2_" + AndExpressionLabel + ":");
        addLine("\t" + "br label %exp_res_3_" + AndExpressionLabel);

        // exp_res_3
        addLine("\t" + "exp_res_3_" + AndExpressionLabel + ":");
        addLine("\t" + "%_" + registerCounter + " = phi i1  [ 0, %exp_res_0_" + AndExpressionLabel + " ], [ "
                + secondClause + ", %exp_res_2_" + AndExpressionLabel + " ]");
        registerCounter++;
        return "%_" + (registerCounter - 1);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(CompareExpression n, Object argu) throws Exception {
        String name1 = n.f0.accept(this, argu);
        String type1 = isVar(name1, (method) argu);
        String name2 = n.f2.accept(this, argu);
        String type2 = isVar(name2, (method) argu);
        String register1;
        String register2;
        method thisMethod = (method) argu;

        if (type1 != null) {
            register1 = loadVarToRegister(name1, type1, thisMethod);
        } else {
            register1 = name1;
        }
        if (type2 != null) {
            register2 = loadVarToRegister(name2, type2, thisMethod);
        } else {
            register2 = name2;
        }
        addLine("\t" + "%_" + registerCounter + " = icmp slt i32 " + register1 + ", " + register2);
        registerCounter++;
        return "%_" + (registerCounter - 1);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(PlusExpression n, Object argu) throws Exception {
        String name1 = n.f0.accept(this, argu);
        String type1 = isVar(name1, (method) argu);
        String name2 = n.f2.accept(this, argu);
        String type2 = isVar(name2, (method) argu);
        String register1;
        String register2;
        method thisMethod = (method) argu;

        if (type1 != null) {
            register1 = loadVarToRegister(name1, type1, thisMethod);
        } else {
            register1 = name1;
        }
        if (type2 != null) {
            register2 = loadVarToRegister(name2, type2, thisMethod);
        } else {
            register2 = name2;
        }
        addLine("\t" + "%_" + registerCounter + " = add i32 " + register1 + ", " + register2);
        registerCounter++;
        return "%_" + (registerCounter - 1);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(MinusExpression n, Object argu) throws Exception {
        String name1 = n.f0.accept(this, argu);
        String type1 = isVar(name1, (method) argu);
        String name2 = n.f2.accept(this, argu);
        String type2 = isVar(name2, (method) argu);
        String register1;
        String register2;
        method thisMethod = (method) argu;

        if (type1 != null) {
            register1 = loadVarToRegister(name1, type1, thisMethod);
        } else {
            register1 = name1;
        }
        if (type2 != null) {
            register2 = loadVarToRegister(name2, type2, thisMethod);
        } else {
            register2 = name2;
        }
        addLine("\t" + "%_" + registerCounter + " = sub i32 " + register1 + ", " + register2);
        registerCounter++;
        return "%_" + (registerCounter - 1);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    @Override
    public String visit(ArrayLookup n, Object argu) throws Exception {
        String arrayName = n.f0.accept(this, argu);
        String arrayType = isVar(arrayName, (method) argu);
        String indexName = n.f2.accept(this, argu);
        String indexType = isVar(indexName, (method) argu);
        String arrayRegister;
        String indexRegister;
        method thisMethod = (method) argu;
        if (arrayType != null) {
            arrayRegister = loadVarToRegister(arrayName, arrayType, thisMethod);
        } else {
            arrayRegister = arrayName;
        }
        if (indexType != null) {
            indexRegister = loadVarToRegister(indexName, indexType, thisMethod);
        } else {
            indexRegister = indexName;
        }

        addLine("\t" + "%_" + registerCounter + " = load i32, i32* " + arrayRegister);
        registerCounter++;
        addLine("\t" + "%_" + registerCounter + " = icmp sge i32 " + indexRegister + ", 0");
        registerCounter++;
        addLine("\t" +
                "%_" + registerCounter + " = icmp slt i32 " + indexRegister + ", %_" + (registerCounter - 2));
        registerCounter++;
        addLine("\t" +
                "%_" + registerCounter + " = and i1 %_" + (registerCounter - 2) + ", %_" + (registerCounter - 1));
        registerCounter++;
        addLine("\t" + "br i1 %_" + (registerCounter - 1) + ", label %oob_ok_" + labelCounter
                + ", label %oob_err_" + labelCounter);

        addLine("\t" + "\noob_err_" + labelCounter + ":");
        addLine("\t" + "call void @throw_oob()");
        addLine("\t" + "br label %oob_out_" + labelCounter);

        addLine("\t" + "\noob_ok_" + labelCounter + ":");
        addLine("\t" + "%_" + registerCounter + " = add i32 1, " + indexRegister);
        registerCounter++;
        addLine("\t" + "%_" + registerCounter + " = getelementptr i32, i32* " + arrayRegister
                + " , i32 %_" + (registerCounter - 1));
        registerCounter++;
        addLine("\t" + "%_" + registerCounter + " = load i32, i32* %_" + (registerCounter - 1));
        addLine("\t" + "br label %oob_out_" + labelCounter);
        addLine("\t" + "\noob_out_" + labelCounter + ":");
        registerCounter++;

        // If the array is of booleand type, convert the i32 value stored to i1
        if (arrayType.equals("boolean[]")) {
            addLine("\t" + "%_" + registerCounter + " = trunc i32 %_" + (registerCounter - 1) + " to i1");
            registerCounter++;
        }
        labelCounter++;

        return "%_" + (registerCounter - 1);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(TimesExpression n, Object argu) throws Exception {
        String name1 = n.f0.accept(this, argu);
        String type1 = isVar(name1, (method) argu);
        String name2 = n.f2.accept(this, argu);
        String type2 = isVar(name2, (method) argu);
        String register1;
        String register2;
        method thisMethod = (method) argu;

        if (type1 != null) {
            register1 = loadVarToRegister(name1, type1, thisMethod);
        } else {
            register1 = name1;
        }
        if (type2 != null) {
            register2 = loadVarToRegister(name2, type2, thisMethod);
        } else {
            register2 = name2;
        }
        addLine("\t" + "%_" + registerCounter + " = mul i32 " + register1 + ", " + register2);
        registerCounter++;
        return "%_" + (registerCounter - 1);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    @Override
    public String visit(ArrayLength n, Object argu) throws Exception {
        String arrayName = n.f0.accept(this, argu);
        String arrayType = isVar(arrayName, (method) argu);
        String arrayRegister;
        method thisMethod = (method) argu;

        if (arrayType != null) {
            arrayRegister = loadVarToRegister(arrayName, arrayType, thisMethod);
        } else {
            arrayRegister = arrayName;
        }
        if (arrayType.equals("int[]")) {
            addLine("\t" + "%_" + registerCounter + " = load i32, i32* " + arrayRegister);
            registerCounter++;
            labelCounter++;
        }
        return "%_" + (registerCounter - 1);
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement n, Object argu) throws Exception {
        method thisMethod = (method) argu;
        String identifier = n.f0.accept(this, argu);
        String type = isVar(identifier, thisMethod);

        // Func to get store address
        String expressionResult = n.f2.accept(this, argu);
        if (isVar(expressionResult, thisMethod) != null) {
            expressionResult = loadVarToRegister(expressionResult, type, thisMethod);
        }
        storeExpression(identifier, type, thisMethod, expressionResult);
        return null;
    }

    /**
     * f0 -> "new"
     * f1 -> "boolean"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public String visit(BooleanArrayAllocationExpression n, Object argu) throws Exception {
        String arraySize = n.f3.accept(this, argu);
        method thisMethod = (method) argu;
        if (isVar(arraySize, thisMethod) != null) {
            arraySize = loadVarToRegister(arraySize, "int", thisMethod);
        }
        addLine("\t" + "%_" + registerCounter + " = add i32 1, " + arraySize);
        registerCounter++;

        addLine("\t" + "%_" + registerCounter + " = icmp sge i32 %_" + (registerCounter - 1) + ", 1");
        registerCounter++;

        addLine("\t" + "br i1 %_" + (registerCounter - 1) + ", label %nsz_ok_" + labelCounter
                + ", label %nsz_err_" + labelCounter);

        addLine("\t" + "\nnsz_err_" + labelCounter + ":");
        addLine("\t" + "call void @throw_nsz()");
        addLine("\t" + "br label %nsz_ok_" + labelCounter);
        addLine("\t" + "\nnsz_ok_" + labelCounter + ":");
        addLine(
                "\t" + "%_" + registerCounter + " = call i8* @calloc(i32 %_" + (registerCounter - 2) + ", i32 4)");
        registerCounter++;

        addLine("\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i32*");
        registerCounter++;

        addLine("\t" + "store i32 " + arraySize + ", i32* %_" + (registerCounter - 1));
        labelCounter++;
        return "%_" + (registerCounter - 1);
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public String visit(IntegerArrayAllocationExpression n, Object argu) throws Exception {
        String arraySize = n.f3.accept(this, argu);
        method thisMethod = (method) argu;
        if (isVar(arraySize, thisMethod) != null) {
            arraySize = loadVarToRegister(arraySize, "int", thisMethod);
        }
        addLine("\t" + "%_" + registerCounter + " = add i32 1, " + arraySize);
        registerCounter++;

        addLine("\t" + "%_" + registerCounter + " = icmp sge i32 %_" + (registerCounter - 1) + ", 1");
        registerCounter++;

        addLine("\t" + "br i1 %_" + (registerCounter - 1) + ", label %nsz_ok_" + labelCounter
                + ", label %nsz_err_" + labelCounter);

        addLine("\t" + "\nnsz_err_" + labelCounter + ":");
        addLine("\t" + "call void @throw_nsz()");
        addLine("\t" + "br label %nsz_ok_" + labelCounter);
        addLine("\t" + "\nnsz_ok_" + labelCounter + ":");
        addLine(
                "\t" + "%_" + registerCounter + " = call i8* @calloc(i32 %_" + (registerCounter - 2) + ", i32 4)");
        registerCounter++;

        addLine("\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i32*");
        registerCounter++;

        addLine("\t" + "store i32 " + arraySize + ", i32* %_" + (registerCounter - 1));
        labelCounter++;
        return "%_" + (registerCounter - 1);
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    @Override
    public String visit(AllocationExpression n, Object argu) throws Exception {
        List<variable> variableList = null;
        classMap parentClass = null;
        String type = n.f1.accept(this, argu);
        lastAlloc = type;
        int fieldOffset = 0;
        parentClass = classes.get(type).parentClass;

        // Calculate the ammount of memory to allocate to the new object
        // Get the offset of the last field of the object, add its size + 8 for the
        // V-Table
        if (classes.get(type).fieldOffset.length == 0) {
            while (parentClass != null) {

                if (!parentClass.methods.containsKey("main")) {
                    if (parentClass.fieldOffset.length != 0) {
                        fieldOffset = parentClass.fieldOffset[parentClass.fieldOffset.length - 1];
                        variableList = new ArrayList<variable>(parentClass.fields.values());
                        if (variableList.get(parentClass.fieldOffset.length - 1).Type == "int") {
                            fieldOffset += 4;
                        } else if (variableList.get(parentClass.fieldOffset.length - 1).Type == "boolean") {
                            fieldOffset += 1;
                        } else {
                            fieldOffset += 8;
                        }
                        break;
                    }
                }
                parentClass = parentClass.parentClass;
            }
        } else {
            fieldOffset = classes.get(type).fieldOffset[classes.get(type).fieldOffset.length - 1];
            variableList = new ArrayList<variable>(classes.get(type).fields.values());
            if (variableList.get(classes.get(type).fieldOffset.length - 1).Type == "int") {
                fieldOffset += 4;
            } else if (variableList.get(classes.get(type).fieldOffset.length - 1).Type == "boolean") {
                fieldOffset += 1;
            } else {
                fieldOffset += 8;
            }
        }
        addLine("\t" + "%_" + registerCounter + " = call i8* @calloc(i32 1, i32 " + (fieldOffset + 8) + ")");
        registerCounter++;

        addLine("\t" + "%_" + registerCounter + " = bitcast i8* %_" + (registerCounter - 1) + " to i8***");
        registerCounter++;

        addLine("\t" +
                "%_" + registerCounter + " = getelementptr [" + getVtableSize(classes.get(type)) + " x i8*], ["
                + getVtableSize(classes.get(type)) + " x i8*]* @." + type + "_vtable, i32 0, i32 0");
        registerCounter++;

        addLine("\t" + "store i8** %_" + (registerCounter - 1) + ", i8*** %_" + (registerCounter - 2));

        return "%_" + (registerCounter - 3);
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    @Override
    public String visit(BracketExpression n, Object argu) throws Exception {
        String type = n.f1.accept(this, argu);
        return type;
    }

    /**
     * f0 -> "!"
     * f1 -> Clause()
     */
    @Override
    public String visit(NotExpression n, Object argu) throws Exception {
        String clause = n.f1.accept(this, argu);
        method thisMethod = (method) argu;
        if (isVar(clause, (method) argu) != null) {
            clause = loadVarToRegister(clause, "boolean", thisMethod);
        }
        addLine("\t" + "%_" + registerCounter + " = xor i1 1, " + clause);
        registerCounter++;
        return "%_" + (registerCounter - 1);
    }

    @Override
    public String visit(BooleanArrayType n, Object argu) {
        return "boolean[]";
    }

    @Override
    public String visit(IntegerArrayType n, Object argu) {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n, Object argu) {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n, Object argu) {
        return "int";
    }

    @Override
    public String visit(Identifier n, Object argu) {
        return n.f0.toString();
    }

    @Override
    public String visit(IntegerLiteral n, Object argu) throws Exception {
        return n.f0.toString();
    }

    @Override
    public String visit(TrueLiteral n, Object argu) throws Exception {
        return n.f0.toString();
    }

    @Override
    public String visit(FalseLiteral n, Object argu) throws Exception {
        return n.f0.toString();
    }
}
