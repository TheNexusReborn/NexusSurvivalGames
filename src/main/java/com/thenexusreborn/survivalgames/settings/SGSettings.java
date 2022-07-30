package com.thenexusreborn.survivalgames.settings;

import java.lang.reflect.Field;

public abstract class SGSettings implements Cloneable {
    protected final String tableName;
    
    public SGSettings(String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public abstract int getId();
    public abstract void setId(int id);
    
    public abstract String getType();
    public abstract void setType(String type);
    
    @SuppressWarnings("SqlResolve")
    public void pushToDatabase() {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
    
        String[] columns = new String[fields.length], values = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            columns[i] = field.getName();
            try {
                values[i] = field.get(this).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    
        String sql;
        if (getId() == 0) {
            StringBuilder columnBuilder = new StringBuilder(), valuesBuilder = new StringBuilder();
            for (int i = 0; i < columns.length; i++) {
                columnBuilder.append(columns[i]).append(",");
                valuesBuilder.append("'").append(values[i]).append("'").append(",");
            }
    
            String c = columnBuilder.substring(0, columnBuilder.length() - 1);
            String v = valuesBuilder.substring(0, valuesBuilder.length() - 1);
            //language=MySQL
            sql = "insert into " + tableName + "(" + c + ") values (" + v + ");";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < columns.length; i++) {
                sb.append(columns[i]).append("='").append(values[i]).append("'").append(",");
            }
    
            //language=MySQL
            sql = "update " + this.tableName + " set " + sb.substring(0, sb.length() - 1) + " where id='" + getId() + "';";
        }
    
        //TODO
//        try (Connection connection = NexusAPI.getApi().getConnection(); Statement statement = connection.createStatement()) {
//            if (getId() == 0) {
//                statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
//                ResultSet generatedKeys = statement.getGeneratedKeys();
//                generatedKeys.next();
//                setId(generatedKeys.getInt(1));
//            } else {
//                statement.executeUpdate(sql);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
    
    @Override
    public abstract SGSettings clone();
}
