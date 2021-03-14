package tech.itpark.repository;

import tech.itpark.entity.UserEntity;
import tech.itpark.exception.DataAccessException;
import tech.itpark.jdbc.RowMapper;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Driver - iface
// Connection - iface
// Statement/PreparedStatement/CallableStatement - iface
// ResultSet - iface
// SQLException -> Exception (checked) - try-catch or signature
// типы SQL'ые

//class UserEntityRowMapper implements RowMapper<UserEntity> {
//
//  @Override
//  public UserEntity map(ResultSet rs) throws SQLException {
//    return new UserEntity(rs.getLong("id"), ...);
//  }
//}

// nested
// inner
// local
// anonymous

// alt + insert - generation
// alt + enter - make
public class UserRepositoryJDBCImpl implements UserRepository {
    private final Connection connection;
    private final RowMapper<UserEntity> mapper = rs -> {
        try {
            return new UserEntity(
                    rs.getLong("id"),
                    rs.getString("login"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("secret"),
                    Set.of((String[]) rs.getArray("roles").getArray()),
                    rs.getBoolean("removed"),
                    rs.getLong("created")
            );
        } catch (SQLException e) {
            // pattern -> "convert" checked to unchecked (заворачивание исключений)
            throw new DataAccessException(e);
        }
    };

    public UserRepositoryJDBCImpl(Connection connection) {
        this.connection = connection;
    }

    // mapper -> map -> objectType1 -> objectType2:
    // rs -> UserEntity
    @Override
    public List<UserEntity> findAll() {
        try (
                final Statement stmt = connection.createStatement();
                final ResultSet rs = stmt.executeQuery(
                        "SELECT id, login, password, name, secret, roles, EXTRACT(EPOCH FROM created) created, removed FROM users ORDER BY id"
                );
        ) {
            List<UserEntity> result = new LinkedList<>();
            while (rs.next()) {
                final UserEntity entity = mapper.map(rs);
                result.add(entity);
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(Long aLong) {
        try (

                final PreparedStatement stmt = connection.prepareStatement(
                        "SELECT id, login, password, name, secret, roles, EXTRACT(EPOCH FROM created) created, remowed FROM id WHERE id = ?"
                );

        ) {
            stmt.setLong(1, aLong);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.ofNullable(mapper.map(rs));
            }


        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return Optional.ofNullable(null);
    }

    @Override
    public UserEntity save(UserEntity entity) {
        if (entity.getId() == 0) {
            try (
                    PreparedStatement stmt = connection.prepareStatement(
                            "INSERT INTO users login, paasword, name, secret, roles, remowed, EXTRACT(EPOCH FROM created) created VALUES (?, ?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
            ) {
                int index = 0;
                stmt.setString(++index, entity.getLogin());
                stmt.setString(++index, entity.getPassword());
                stmt.setString(++index, entity.getName());
                stmt.setString(++index, entity.getSecret());
                stmt.setArray(index++, connection.createArrayOf("TEXT", entity.getRoles().toArray()));
                stmt.setBoolean(++index, entity.isRemoved());
                stmt.setLong(++index, entity.getCreated());

                stmt.execute();

                try (ResultSet keys = stmt.getGeneratedKeys();) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        return entity;
                    }
                    throw new DataAccessException("No keys generated");
                }

            } catch (SQLException e) {
                throw new DataAccessException(e);
            }
        }
        try (
                final PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE users SET login = ?, password = ?, name =?, secret = ?, roles = ?, remowed = ?, created = ? WHERE  id =?"
                );
        ) {
            int index = 0;
            stmt.setString(++index, entity.getLogin());
            stmt.setString(++index, entity.getPassword());
            stmt.setString(++index, entity.getName());
            stmt.setString(++index, entity.getSecret());
            stmt.setArray(index++, connection.createArrayOf("TEXT", entity.getRoles().toArray()));
            stmt.setBoolean(++index, entity.isRemoved());
            stmt.setLong(++index, entity.getCreated());

            stmt.execute();

            return entity;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }


    @Override
    public boolean removeById(Long aLong) {
        try (
                final PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE id = ?");
        ) {
            stmt.setLong(1, aLong);
            return stmt.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public boolean existsByLogin(String login) {
        try (
                final PreparedStatement stmt = connection.prepareStatement(
                        "SELECT id, login, password, name, secret, roles, remowed, EXTRACT(EPOCH FROM created) created FROM users WHERE login =?"
                );
        ) {
            stmt.setString(1, login);
            try (final ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return false;
    }

    @Override
    public Optional<UserEntity> findByLogin(String login) {
        try (
                final PreparedStatement stmt = connection.prepareStatement(
                        "SELECT id, login, password, name, secret, roles, remowed, EXTRACT(EPOCH FROM created)created FROM users WHERE login = ?"
                );
                ){
            stmt.setString(1,login);
            try (final ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    return Optional.ofNullable(mapper.map(rs));
                }
            }

        }catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return Optional.of(null);
    }
}

