package Service;

import Repo.BetRepo;
import domain.Bet;
import domain.Bookmaker;
import domain.EventOdd;
import domain.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BetService {

    private BetRepo betRepo;

    @Autowired
    public BetService(BetRepo betRepo) {
        this.betRepo = betRepo;
    }

    public void createBet(Game game, Bookmaker bookmaker, EventOdd eventOdd, int event, double odd, Date addTime, double ratio) {
        betRepo.createBet(game, bookmaker, eventOdd, event, odd, addTime, ratio);
    }

    public List<Bet> findNotСalculatedBets() {
        return betRepo.findNotСalculatedBets();
    }

    public void setWinResult(Bet bet) {
        setBetResult(bet, 1);
    }

    public void setLoseResult(Bet bet) {
        setBetResult(bet, -1);
    }

    public void setBetResult(Bet bet,int result) {
        betRepo.setBetResult(bet, result);
    }

    public Bet findBetByEvenOdd(EventOdd eventOdd) {
        return betRepo.findBetByEvenOdd(eventOdd);
    }

}
