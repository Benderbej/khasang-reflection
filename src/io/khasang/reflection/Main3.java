package io.khasang.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Main3 {
    public static void main(String[] args) throws NoSuchMethodException {

        //Class<TestSomeExtendedExtended> manager = TestSomeExtendedExtended.class;
        //Class<TestAbstract> manager = TestAbstract.class;
        Class<Test> manager = Test.class;

        Package aPackage = manager.getPackage();
        System.out.printf("package %s;%n", aPackage.getName());

        int modifiers = manager.getModifiers(); // 101010101
//        Modifier
//        modifiers = 1; // 00001
//        modifiers = 3; // 00011
//        modifiers = 5; // 00101
//        System.out.println(Modifier.isPublic(modifiers));
//        System.out.println(Modifier.isPrivate(modifiers));
//        System.out.println(Modifier.toString(modifiers));


        System.out.printf("%s %s %s", Modifier.toString(modifiers),
        manager.isInterface() ? "interface" : "class", manager.getSimpleName());


        //?????????? ВЕРНО ЛИ ВСЕ ЭТО РАССУЖДЕНИЕ????
        //если Class<TestAbstract> manager = TestAbstract.class;
//        String s = manager.getSuperclass().toString();//экземпляр класса Object.class то есть экземпляр класса класса объекта
//        System.out.println("\ns="+s+"\n");
//        String s2 = new Object().toString();//конкретный экземпляр объекта класса объект
//        System.out.println("\ns2="+s2+"\n");

        //String s3 = manager.getSuperclass().getSuperclass().toString();//здесь ловим налпойнтер(если наш класс ничего не экстендит, например Test), потому что суперклассом объекта Object.class является собственно Object
        //System.out.println("\ns3="+s3+"\n");

        //то есть как бы иерархия типов для SomeClass такова SomeClass.class <- Object.class <- Object

        //то есть getSuperClass() возвращает объекты классов Class то есть пареметризованные типы Class<T>, а не собственно типы

        //таким образом нам нужно прокруивать в цикле getSuperClass пока мы не уткнемся в тип  Object
        //SomeClass.class <- Object.class <- Object
        //SomeChildClass.class <- SomeClass.class <- Object.class <- Object

        //посему, метод проверяющий есть ли у нашего класса/типа предок, отличный от Object можно организовать следующим образом:
        //(создадим для этого параметризованный метод)
        if(hasSuperClassButNotObject(manager)){
            System.out.printf(" extends %s", manager.getSuperclass().getSimpleName());
        };

        Class<?>[] interfaces = manager.getInterfaces();
        if (interfaces.length != 0 ) {
            System.out.printf(" implements ");
            for (int i = 0; i < interfaces.length; i++) {
                Class<?> anInterface = interfaces[i];
                System.out.print(i == 0 ? "" : ", ");
                System.out.print(anInterface.getSimpleName());
            }
        }
        System.out.println(" {");

        Field[] fields = manager.getDeclaredFields();

        for (Field field : fields) {
            System.out.printf("\t%s %s %s;%n", Modifier.toString(field.getModifiers()), field.getType().getSimpleName(), field.getName());
        }

        System.out.println();

        Constructor<?>[] declaredConstructors = manager.getDeclaredConstructors();
        for (Constructor<?> constructor : declaredConstructors) {
            System.out.printf("\t%s %s(%s) {}%n",
                    Modifier.toString(constructor.getModifiers()),
                    manager.getSimpleName(),
                    getParameters(constructor.getParameterTypes()));
        }

        System.out.println();

        Method[] methods = manager.getDeclaredMethods();
        for (Method method : methods) {
            System.out.printf("\t%s %s(%s) {}%n",
                    Modifier.toString(method.getModifiers()),
                    manager.getSimpleName(),
                    getParameters(method.getParameterTypes()));
        }

        System.out.println("}");
    }

    private static String getParameters(Class<?>[] parametersType) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parametersType.length; i++) {
            Class<?> parameter = parametersType[i];
            result.append(i == 0 ? "" : ", ")
                    .append(parameter.getSimpleName())
                    .append(" p")
                    .append(i);
        }
        return result.toString();
    }

    private static boolean hasSuperClassButNotObject(Class<?> type){
        int generationLevel=0;
        while(type != null) {
            //System.out.println("type.toString()="+type.toString());
            type = (Class<?>) type.getSuperclass();
            generationLevel++;
            if(generationLevel > 2){return true;}
        }
        return false;
    }
}


//        The thing is that the s.getClass().getSuperclass() never returns null, even though Object has no superclass.
//        I don't understand why this is happening, although I debugged the project several times.
//        https://stackoverflow.com/questions/22047074/object-getclass-getsuperclass-not-returning-null

//        Поскольку в Java отсутствует множественное наследование,
//        то для получения всех предков следует рекурсивно вызвать метод getSuperclass() в цикле,
//        пока не будет достигнут Object, являющийся родителем всех классов.
//        Object не имеет родителей, поэтому вызов его метода getSuperclass() вернет null.
//        http://java-online.ru/java-reflection.xhtml

//        http://samag.ru/archive/article/28