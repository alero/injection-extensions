package test.org.hrodberaht.inject.extension.ejbunit.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Inject extension TDD
 *
 * @author Robert Alexandersson
 *         2011-05-03 20:10
 * @created 1.0
 * @since 1.0
 */
@Entity
public class Customer {

    @Id
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
