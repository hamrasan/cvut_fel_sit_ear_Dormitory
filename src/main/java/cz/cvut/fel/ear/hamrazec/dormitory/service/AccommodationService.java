package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.AccommodationDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Status;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AccommodationService {

    AccommodationDao acoDao;
    StudentDao studentDao;

    @Autowired
    public AccommodationService(AccommodationDao acoDao, StudentDao studentDao) {

        this.acoDao = acoDao;
        this.studentDao = studentDao;
    }

    public List<Accommodation> findAll() {
        return acoDao.findAll();
    }

    public Accommodation find(Long id) {
        return acoDao.find(id);
    }

    @Transactional
    public void create(Accommodation accommodation, Long idStudent) throws NotFoundException {
        //TODO - pridat izbu, kontrolovat volnost izby v tom datume
        Student student = studentDao.find(idStudent);
        if (student == null) throw new NotFoundException();

        acoDao.persist(accommodation);
        student.addAccommodation(accommodation);
        studentDao.update(student);
    }



    //TODO - create na náhodnu izbu

    //TODO - reserve na konkretnu izbu a nahodnu izbu


    @Transactional
    public void delete(Long id) throws NotFoundException {

        Accommodation accommodation = acoDao.find(id);
        if (accommodation == null) throw new NotFoundException();
        cancelAccommodation(accommodation);
    }


    public void updateExpired(List<Accommodation> accommodations){
        //TODO - zmenit na scheduled
        for (Accommodation accommodation: accommodations) {
            if (accommodation.getDateEnd().isBefore(LocalDate.now())) {
                accommodation.setStatus(Status.ENDED);
                acoDao.update(accommodation);
            }
        }

    }


    @Transactional
    public void cancelAccommodation(Accommodation accommodation) {
        setStatusAndUnusualEnd(accommodation,Status.CANCELED);
        accommodation.getRoom().cancelActualAccomodation(accommodation);
        accommodation.getRoom().addPastAccomodation(accommodation);
    }

    @Transactional
    public void cancelReservation(Accommodation accommodation) {
        setStatusAndUnusualEnd(accommodation,Status.RESERVATION_CANCELED);
    }

    private void setStatusAndUnusualEnd(Accommodation accommodation,Status status){
        accommodation.setStatus(status);
        accommodation.setDateUnusualEnd(LocalDate.now());
        acoDao.update(accommodation);
    }
}
