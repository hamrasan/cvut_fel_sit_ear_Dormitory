package cz.cvut.fel.ear.hamrazec.dormitory.model;

import javax.persistence.Entity;

@Entity
public class SuperUser extends Manager {
    public SuperUser() {
        setRole(Role.SUPERUSER);
    }
}
