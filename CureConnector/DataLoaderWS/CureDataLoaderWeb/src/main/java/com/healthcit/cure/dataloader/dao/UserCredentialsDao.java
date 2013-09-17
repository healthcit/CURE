package com.healthcit.cure.dataloader.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.healthcit.cure.dataloader.beans.UserCredentials;


public class UserCredentialsDao {

	public static final String ACCOUNT_ID = "account_id";
	public static final String TOKEN = "token";
	public static final String ACCOUNT_DESCRIPTION = "description";
	public static final String ACCOUNT_ENABLED = "enabled";
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private static final Logger log = LoggerFactory.getLogger( UserCredentialsDao.class );
	
	 public void setJdbcDataSource(DataSource dataSource) {
	        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	    }
	
	public void createAccount(UserCredentials userCredentials)
	{
		Map<String, String> accountDetails = new HashMap<String, String>();
		accountDetails.put(ACCOUNT_ID, userCredentials.getAccountId());
		accountDetails.put(TOKEN, userCredentials.getToken());
		accountDetails.put(ACCOUNT_DESCRIPTION, userCredentials.getDescription());
		log.debug("accountId: " + userCredentials.getAccountId());
		log.debug("token: " + userCredentials.getToken());
		log.debug("description: " + userCredentials.getDescription());
		
		//String select = "SELECT count(*) FROm form WHERE uuid=:uuid";
		String insert =" INSERT INTO ws_accounts (account_id, token, description, enabled) VALUES (:account_id, :token, :description, 'true')";
		
		jdbcTemplate.update(insert, accountDetails);
		
		
	}
	
	
	public int verifyAccount(UserCredentials userCredentials)
	{
		Map<String, String> accountDetails = new HashMap<String, String>();
		accountDetails.put(ACCOUNT_ID, userCredentials.getAccountId());
		accountDetails.put(TOKEN, userCredentials.getToken());
		log.debug("accountId: " + userCredentials.getAccountId());
		log.debug("token: " + userCredentials.getToken());
		
		//String select = "SELECT count(*) FROm form WHERE uuid=:uuid";
		String select =" SELECT count(*) FROM ws_accounts where account_id=:account_id AND token = :token and enabled='true'";
		
		return jdbcTemplate.queryForInt(select, accountDetails);
	}
	
	
	public List<UserCredentials> getAllAccounts()
	{
		String select =" SELECT account_id, token, description, enabled FROM ws_accounts order by account_id";
		List<UserCredentials> list = jdbcTemplate.query(select, new HashMap(), new UserCredentialsMapper());
		
		//return (List(UserCredentials))jdbcTemplate.queryForObject(select, new HashMap(), new UserCredentialsMapper());
		return list;
	}
	
	public UserCredentials getAccount(String accountId)
	{
		Map<String, String> accountDetails = new HashMap<String, String>();
		accountDetails.put(ACCOUNT_ID, accountId);
		String select =" SELECT account_id, token, description, enabled FROM ws_accounts where account_id=:account_id";
		
		return jdbcTemplate.queryForObject(select, accountDetails, new UserCredentialsMapper());
	}
	
	public void delete(String accountId)
	{
		Map<String, String> accountDetails = new HashMap<String, String>();
		accountDetails.put(ACCOUNT_ID, accountId);
		String delete =" DELETE FROM ws_accounts WHERE account_id=:account_id";
		
		jdbcTemplate.update(delete, accountDetails);
	}
	
	public void updateEnabled(String accountId, boolean enabled)
	{
		Map<String, Object> accountDetails = new HashMap<String, Object>();
		accountDetails.put(ACCOUNT_ID, accountId);
		accountDetails.put(ACCOUNT_ENABLED, enabled);
		String update ="UPDATE ws_accounts set enabled=:enabled WHERE account_id=:account_id";
		jdbcTemplate.update(update, accountDetails);
	}
	
	
}

class UserCredentialsMapper implements RowMapper<UserCredentials>
{
     public UserCredentials mapRow(ResultSet rs, int rowNum) throws SQLException {
            String deptId = rs.getString("account_id");
            UserCredentials credentials = new UserCredentials();
            credentials.setAccountId(rs.getString("account_id"));
            credentials.setToken(rs.getString("token"));
            credentials.setDescription(rs.getString("description"));
            credentials.setEnabled(rs.getBoolean("enabled"));
            return credentials;
        }
}