package domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "game")
public class Game {

    @Id
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    private  League league;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_home_id")
    private Team teamHome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_away_id")
    private Team teamAway;

    private int status;

    @Column(name = "scores_home")
    private int scoresHome;

    @Column(name = "scores_away")
    private int scoresAway;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<EventOdd> eventOdds;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Team getTeamHome() {
        return teamHome;
    }

    public void setTeamHome(Team teamHome) {
        this.teamHome = teamHome;
    }

    public Team getTeamAway() {
        return teamAway;
    }

    public void setTeamAway(Team teamAway) {
        this.teamAway = teamAway;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getScoresHome() {
        return scoresHome;
    }

    public void setScoresHome(int scoresHome) {
        this.scoresHome = scoresHome;
    }

    public int getScoresAway() {
        return scoresAway;
    }

    public void setScoresAway(int scoresAway) {
        this.scoresAway = scoresAway;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = new Date((long)date*1000);
    }

    public List<EventOdd> getEventOdds() {
        return eventOdds;
    }

    public void setEventOdds(List<EventOdd> eventOdds) {
        this.eventOdds = eventOdds;
    }
}
