package io.pivotal.gemfire.sample.app.serialization;

import io.pivotal.gemfire.sample.app.entity.Person;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;

/**
 * Created by Charlie Black on 7/5/17.
 */
public class PersonPdxSerializer implements PdxSerializer {

    public static final String VERSION = "version";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String AGE = "age";

    /**
     * This method is given an object to serialize as a PDX using the given writer.
     * If it chooses to do the serialization then it must return <code>true</code>;
     * otherwise it must return <code>false</code> in which case it will be serialized using
     * standard serialization.
     *
     * @param o   the object to consider serializing as a PDX
     * @param out the {@link PdxWriter} to use to serialize the object
     * @return <code>true</code> if the method serialized the object; otherwise <code>false</code>
     */
    @Override
    public boolean toData(Object o, PdxWriter out) {
        if (!(o instanceof Person)) {
            return false;
        }
        Person person = (Person) o;
        out.writeByte(VERSION, (byte) 1);
        out.writeInt(ID, person.getId());
        out.writeString(NAME, person.getName());
        out.writeInt(AGE, person.getAge());
        return true;
    }

    /**
     * This method is given an class that should be
     * instantiated and deserialized using the given reader.
     *
     * @param clazz the Class of the object that should be deserialized
     * @param in    the reader to use to obtain the field data
     * @return the deserialized object. <code>null</code> indicates that this
     * PdxSerializer does not know how to deserialize the given class.
     */
    @Override
    public Object fromData(Class<?> clazz, PdxReader in) {

        if (!Person.class.equals(clazz)) {
            return null;
        }

        Person person = new Person();

        byte version = in.readByte(VERSION);
        person.setId(in.readInt(ID));
        person.setName(in.readString(NAME));
        person.setAge(in.readInt(AGE));

        return person;
    }
}
