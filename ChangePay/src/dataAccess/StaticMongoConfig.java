package dataAccess;

public class StaticMongoConfig {
	public static String DB_HOST_NAME;
	public static String DB_PORT_NUMBER;
	public static String DB_PASSWORD;
	public static String DB_USER_NAME;
	
	
	public static String getDB_PORT_NUMBER() {
		return DB_PORT_NUMBER;
	}
	public static void setDB_PORT_NUMBER(String dB_PORT_NUMBER) {
		DB_PORT_NUMBER = dB_PORT_NUMBER;
	}
	public static String getDB_PASSWORD() {
		return DB_PASSWORD;
	}
	public static void setDB_PASSWORD(String dB_PASSWORD) {
		DB_PASSWORD = dB_PASSWORD;
	}
	public static String getDB_HOST_NAME() {
		return DB_HOST_NAME;
	}
	public static void setDB_HOST_NAME(String dB_HOST_NAME) {
		DB_HOST_NAME = dB_HOST_NAME;
	}
	public static String getDB_USER_NAME() {
		return DB_USER_NAME;
	}
	public static void setDB_USER_NAME(String dB_USER_NAME) {
		DB_USER_NAME = dB_USER_NAME;
	}
}
