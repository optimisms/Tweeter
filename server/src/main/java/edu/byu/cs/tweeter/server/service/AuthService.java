package edu.byu.cs.tweeter.server.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class AuthService {
    public static boolean tokenIsValid(AuthToken token) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:m");

        Date authDate = sdf.parse(token.getDatetime());
        Date now = Calendar.getInstance().getTime();

        long diffInMillies = Math.abs(authDate.getTime() - now.getTime());
        long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

        System.out.println("time since auth token: " + diff);

        return diff <= 30;
    }
}
