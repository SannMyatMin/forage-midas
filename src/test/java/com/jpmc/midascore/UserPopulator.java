package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPopulator {
    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private DatabaseConduit databaseConduit;

    public void populate() {
        String[] userLines = fileLoader.loadStrings("/test_data/lkjhgfdsa.hjkl");

        for (int i = 0; i < userLines.length - 1; i += 2) {
            String username = userLines[i].trim();
            String balanceStr = userLines[i + 1].trim();
            float balance = Float.parseFloat(balanceStr);

            UserRecord user = new UserRecord(username, balance);
            databaseConduit.save(user);
        }
    }

}
