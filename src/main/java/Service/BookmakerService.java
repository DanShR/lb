package Service;

import Repo.BookmakerRepo;
import domain.Bookmaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmakerService {

    private BookmakerRepo bookmakerRepo;

    @Autowired
    public BookmakerService(BookmakerRepo bookmakerRepo) {
        this.bookmakerRepo = bookmakerRepo;
    }

    public Bookmaker createBookmaker(String name) {
        return bookmakerRepo.createBookmaker(name);
    }

    public Bookmaker getBookmaker(String name) {

        Bookmaker bookmaker = null;
        bookmaker = findBookmakerByName(name);
        if (bookmaker == null)
            bookmaker = createBookmaker(name);
        return bookmaker;
    }

    public Bookmaker findBookmakerByName(String name) {
        return bookmakerRepo.findBookmakerByName(name);
    }

    public List<Bookmaker> findAllBookmaker() {
        return  bookmakerRepo.findAllBookmaker();
    }
}
