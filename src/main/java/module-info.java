import org.jspecify.annotations.NullMarked;

@NullMarked
module dev.mccue.todoapp {
    requires org.microhttp;
    requires dev.mccue.microhttp.json;
    requires dev.mccue.urlparameters;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j.simple;
}