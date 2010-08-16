package test.com.hrodberaht.inject.extension.transaction;

import test.com.hrodberaht.inject.extension.transaction.example.Logging;
import test.com.hrodberaht.inject.extension.transaction.example.Person;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: Robert Work
 * Date: 2010-aug-15
 * Time: 20:09:01
 * To change this template use File | Settings | File Templates.
 */
public class StubUtil {
    private static AtomicLong id = new AtomicLong(1L);

    public static long getNextId(){
         return id.incrementAndGet();
    }

    public static Person createPerson() {
        Person person = new Person();
        person.setId(getNextId());
        person.setName("Dude");
        return person;
    }

    public static Logging createLogg(String message){
        Logging logg = new Logging();
        logg.setId(getNextId());
        logg.setMessage(message);
        return logg;
    }
}
