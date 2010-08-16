package test.com.hrodberaht.inject.extension.transaction.example;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by IntelliJ IDEA.
 * User: Robert Work
 * Date: 2010-aug-16
 * Time: 15:53:34
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class Logging {

    @Id
    private Long id;

    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
