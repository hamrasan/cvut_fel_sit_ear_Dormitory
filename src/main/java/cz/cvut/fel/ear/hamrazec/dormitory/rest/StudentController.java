package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import cz.cvut.fel.ear.hamrazec.dormitory.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/students")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_SUPERUSER')")
public class StudentController {


    private static final Logger LOG = LoggerFactory.getLogger(StudentController.class);

    private StudentService studentService;


    @Autowired
    public StudentController(StudentService service) {

        this.studentService = service;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Student> getStudents() {

        return studentService.findAll();
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Student getStudent(@PathVariable Long id) throws NotFoundException {

        Student student = studentService.find(id);
        if (student == null) throw new NotFoundException();
        return student;
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Student getStudentMe() throws NotFoundException {

        Student student = studentService.findMe();
        if (student == null) throw new NotFoundException();
        return student;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createStudent(@Valid @RequestBody Student student) throws NotAllowedException {

        studentService.create(student);
        LOG.info("Student with id {} created.", student.getId());
    }



    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStudent(@PathVariable Long id, @RequestBody Student student) throws NotFoundException, NotAllowedException {

        studentService.update(id, student);
        LOG.info("Student with id {} updated.", id);
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void removeStudent(@PathVariable Long id) throws NotFoundException, NotAllowedException {

        studentService.delete(id);
        LOG.info("Student with id {} removed.", id);
    }


}
