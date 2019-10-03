package Service;

import Repo.CountryRepo;
import domain.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryService {

    private CountryRepo countryRepo;

    @Autowired
    public CountryService(CountryRepo countryRepo) {
        this.countryRepo = countryRepo;
    }

    public Country findCountryByCode(String code) {
        return countryRepo.findCountryByCode(code);
    }

}
