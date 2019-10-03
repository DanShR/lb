package Service;

import Repo.EventOddRepo;
import domain.Bookmaker;
import domain.EventOdd;
import domain.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventOddService {

    private EventOddRepo eventOddRepo;

    @Autowired
    public EventOddService(EventOddRepo eventOddRepo) {
        this.eventOddRepo = eventOddRepo;
    }

    public EventOdd createUpdateEventOdd(int id, Game game, Bookmaker bookmaker, double homeOdd, double drawOdd, double awayOdd, int timeStr, int addTime) {
        EventOdd eventOdd = null;
        eventOdd = findEventOddById(id);
        if (eventOdd == null)
            eventOdd = createEventOdd(id, game, bookmaker, homeOdd, drawOdd, awayOdd, timeStr, addTime);
        else
            updateEventOdd(eventOdd, game, bookmaker, homeOdd, drawOdd, awayOdd, timeStr, addTime);
        return eventOdd;
    }

    public void updateEventOdd(EventOdd eventOdd, Game game, Bookmaker bookmaker, double homeOdd, double drawOdd, double awayOdd, int timeStr, int addTime) {
        eventOddRepo.updateEventOdd(eventOdd, game, bookmaker, homeOdd, drawOdd, awayOdd, timeStr, addTime);
    }


    public EventOdd createEventOdd(int id, Game game, Bookmaker bookmaker, double homeOdd, double drawOdd, double awayOdd, int timeStr, int addTime) {
        return eventOddRepo.createEventOdd(id, game, bookmaker, homeOdd, drawOdd, awayOdd, timeStr, addTime);
    }

    public EventOdd findEventOddById(int id) {
        return  eventOddRepo.findEventOddById(id);
    }

    public List<EventOdd> gameEventOdds(Game game) {
        return eventOddRepo.gameEventOdds(game);
    }
}
