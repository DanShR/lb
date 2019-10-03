package Service;

import Repo.LeagueRepo;
import com.fasterxml.jackson.databind.JsonNode;
import domain.Country;
import domain.League;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeagueService {

    private CountryService countryService;
    private LeagueRepo leagueRepo;

    @Autowired
    public LeagueService(CountryService countryService, LeagueRepo leagueRepo) {
        this.countryService = countryService;
        this.leagueRepo = leagueRepo;
    }

    public League getLeagueFromJsonNode(JsonNode node) {
        League league= null;
        int id = node.get("id").asInt();
        league = findLeagueById(id);
        if (league != null) {
            return league;
        } else {
            String name = node.get("name").asText();
            Country country = countryService.findCountryByCode(node.get("cc").asText());
            league = createLeague(id, name, country);
            return league;
        }
    }

    public League findLeagueById(int id) {
        return  leagueRepo.findLeagueById(id);
    }

    public League createLeague(int id, String name, Country country) {
        return leagueRepo.createLeague(id, name, country);
    }
}
