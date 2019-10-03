package Service;

import Repo.TeamRepo;
import com.fasterxml.jackson.databind.JsonNode;
import domain.Country;
import domain.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private CountryService countryService;
    private TeamRepo teamRepo;

    @Autowired
    public TeamService(CountryService countryService, TeamRepo teamRepo) {
        this.countryService = countryService;
        this.teamRepo = teamRepo;
    }

    public Team getTeamFromJsonNode(JsonNode node) {
        Team team = null;
        int id = node.get("id").asInt();
        team = findTeamById(id);
        if (team != null) {
            return team;
        } else {
            String name = node.get("name").asText();
            String image_id = node.get("image_id").asText();
            Country country = countryService.findCountryByCode(node.get("cc").asText());
            team = createTeam(id, name, country, image_id);
            return team;
        }
    }

    public Team findTeamById(int id) {
        return  teamRepo.findTeamById(id);
    }

    public Team createTeam(int id, String name, Country country, String image_id) {
        return teamRepo.createTeam(id, name, country, image_id);
    }

}
