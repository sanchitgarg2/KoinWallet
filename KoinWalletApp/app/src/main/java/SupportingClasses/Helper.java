package SupportingClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Coinclasses.Currency;
import Coinclasses.Currency.*;
import Coinclasses.User;
import Coinclasses.WalletSection;
import sharetest.com.coinwallet.Watchlist;

/**
 * Anmol Gupta, LogicMationStudios -  25/01/18.
 */

public class Helper {

    public static final String AppURL="http://192.168.0.126:8080//Koinwallet/KoinWallet/";
    public static final String UserURL="getUser?userID=";
    public static final String CurrencyURL="getUpdate";
    public static final String bitcoinURL ="https://api.cryptonator.com/api/full/btc-usd";
    public static final String addURL="add";
    public static final String tradeURL="Trade";
    public static final String watchlistURL="getWatchList";
    public static final String updatewatchlistURL="updateWatchList";
    public static final String CurrencyListURL="getCurrencyList";
    public static int AppuserID;
    public static HashMap<String, CurrencySnapShot> CURRENCYLIST = new HashMap<String, CurrencySnapShot>();
    public static WalletSection WALLETSECTION= new WalletSection();
    public static User USER=new User();
    public static final String  REGISTERURL="Register";
    public static final String  LOGINURL="Login";
}
