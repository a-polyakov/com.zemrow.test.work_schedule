package com.zemrow.test.work_schedule.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.zemrow.test.work_schedule.entity.AbstractEntity;

/**
 * Универсальное DAO (data access object) реализующее базовые методы работы с хранилищем
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public abstract class AbstractDao<T extends AbstractEntity> {

    public static final String ID = "id";

    /**
     * @return Имя таблицы в БД.
     */
    protected abstract String getTableName();

    /**
     * @return Перечень колонок.
     */
    protected abstract String[] getColumns();

    /**
     * Кеш перечня всех колонок через запятую.
     */
    private String allColumns;

    /**
     * @return Имена всех колонок через запятую.
     */
    public String getAllColumns() {
        if (allColumns == null) {
            synchronized (this) {
                if (allColumns == null) {
                    allColumns = String.join(",", getColumns());
                }
            }
        }
        return allColumns;
    }

    /**
     * Кеш перечня колонок через запятую без идентификатора
     */
    private String columnsWithoutId;

    /**
     * @return Имена колонок через запятую без идентификатора
     */
    public String getColumnsWithoutId() {
        if (columnsWithoutId == null) {
            synchronized (this) {
                if (columnsWithoutId == null) {
                    final StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < getColumns().length - 1; i++) {
                        sb.append(getColumns()[i]);
                        sb.append(',');
                    }
                    sb.append(getColumns()[getColumns().length - 1]);
                    columnsWithoutId = sb.toString();
                }
            }
        }
        return columnsWithoutId;
    }

    /**
     * Прочитать объект
     * @param resultSet Данные из БД
     * @return Объект
     * @throws SQLException
     */
    protected abstract T read(final ResultSet resultSet) throws SQLException;

    /**
     * Записать объект
     * @param statement Интерфейс БД
     * @param entity Объект
     * @throws SQLException
     */
    protected abstract void write(PreparedStatement statement, T entity) throws SQLException;

    /**
     * Записать объект (идентификатор будет сгенерировант БД)
     * @param statement Интерфейс БД
     * @param entity Объект
     * @throws SQLException
     */
    protected abstract void writeWithoutId(PreparedStatement statement, T entity) throws SQLException;

    /**
     * Кеш запроса
     */
    private String querySelectById;

    /**
     * Получить entity по id
     *
     * @param connection Подключение к БД.
     * @param id Идентификатор.
     * @return Объект.
     */
    public T selectById(final Connection connection, final long id) throws SQLException {
        if (querySelectById == null) {
            synchronized (this) {
                if (querySelectById == null) {
                    final StringBuilder sb = new StringBuilder("select ");
                    sb.append(getAllColumns());
                    sb.append(" from ");
                    sb.append(getTableName());
                    sb.append(" where ");
                    sb.append(ID);
                    sb.append("=?");
                    querySelectById = sb.toString();
                }
            }
        }
        try (final PreparedStatement statement = connection.prepareStatement(querySelectById)) {
            statement.setLong(1, id);
            final ResultSet resultSet = statement.executeQuery();
            T result = null;
            if (resultSet.next()) {
                result = read(resultSet);
            }
            return result;
        }
    }

    /**
     * Кеш запроса
     */
    private String querySelect;

    /**
     * Получить набор объектов отсортированый по идентификатору, пропуская id <= fromId и только в количестве limit
     * @param connection Подключение к БД
     * @param fromId
     * @param limit
     * @return Набор объектов.
     * @throws SQLException
     */
    public List<T> select(final Connection connection, final long fromId, final int limit) throws SQLException {
        if (querySelect == null) {
            synchronized (this) {
                if (querySelect == null) {
                    final StringBuilder sb = new StringBuilder("select ");
                    sb.append(getAllColumns());
                    sb.append(" from ");
                    sb.append(getTableName());
                    sb.append(" where ");
                    sb.append(ID);
                    sb.append(">? order by ");
                    sb.append(ID);
                    sb.append(" limit ?");
                    querySelect = sb.toString();
                }
            }
        }
        try (final PreparedStatement statement = connection.prepareStatement(querySelect)) {
            statement.setLong(1, fromId);
            statement.setInt(2, limit);
            final ResultSet resultSet = statement.executeQuery();
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(read(resultSet));
            }
            return result;
        }
    }

    /**
     * Кеш запроса
     */
    private String queryInsert;
    /**
     * Кеш запроса
     */
    private String queryInsertWithoutId;

    /**
     * Добавление записи в таблицу
     *
     * @param connection Подключение к БД.
     * @param entity Объект.
     */
    public void insert(final Connection connection, T entity) throws SQLException {
        if (entity.getId() != null) {
            if (queryInsert == null) {
                synchronized (this) {
                    if (queryInsert == null) {
                        final StringBuilder sb = new StringBuilder("insert into ");
                        sb.append(getTableName());
                        sb.append(" (");
                        sb.append(getAllColumns());
                        sb.append(") VALUES (");
                        for (int i = 1; i < getColumns().length; i++) {
                            sb.append("?,");
                        }
                        sb.append("?)");
                        queryInsert = sb.toString();
                    }
                }
            }
            try (final PreparedStatement statement = connection.prepareStatement(queryInsert)) {
                write(statement, entity);
                statement.execute();
            }
        }
        else {
            if (queryInsertWithoutId == null) {
                synchronized (this) {
                    if (queryInsertWithoutId == null) {
                        final StringBuilder sb = new StringBuilder("insert into ");
                        sb.append(getTableName());
                        sb.append(" (");
                        sb.append(getColumnsWithoutId());
                        sb.append(") VALUES (");
                        for (int i = 2; i < getColumns().length; i++) {
                            sb.append("?,");
                        }
                        sb.append("?)");
                        queryInsertWithoutId = sb.toString();
                    }
                }
            }
            try (final PreparedStatement statement = connection.prepareStatement(queryInsertWithoutId, Statement.RETURN_GENERATED_KEYS)) {
                writeWithoutId(statement, entity);
                statement.executeUpdate();
                final ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    /**
     * Кеш запроса
     */
    private String queryUpdate;

    /**
     * Обновление записи в таблице по id
     *
     * @param connection Подключение к БД.
     * @param entity Объект.
     */
    public void update(final Connection connection, T entity) throws SQLException {
        if (queryUpdate == null) {
            synchronized (this) {
                if (queryUpdate == null) {
                    final StringBuilder sb = new StringBuilder("update ");
                    sb.append(getTableName());
                    sb.append(" set ");
                    for (int i = 1; i < getColumns().length - 1; i++) {
                        sb.append(getColumns()[i]);
                        sb.append("=?,");
                    }
                    sb.append(getColumns()[getColumns().length - 1]);
                    sb.append("=? ");
                    sb.append("where ");
                    sb.append(ID);
                    sb.append("=?");
                    queryUpdate = sb.toString();
                }
            }
        }
        try (final PreparedStatement statement = connection.prepareStatement(queryUpdate)) {
            writeWithoutId(statement, entity);
            statement.setLong(getColumns().length, entity.getId());
            statement.execute();
        }
    }

    /**
     * Удаление записи в таблице по id
     * @param connection Подключение к БД.
     * @param id Идентификатор.
     * @throws SQLException
     */
    public void delete(final Connection connection, final long id) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("delete " + getTableName() + " where id=?")) {
            statement.setLong(1, id);
            statement.execute();
        }
    }
}
