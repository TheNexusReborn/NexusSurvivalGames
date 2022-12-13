package com.thenexusreborn.survivalgames.settings.object;

import com.thenexusreborn.api.frameworks.value.*;
import com.thenexusreborn.api.storage.annotations.*;
import com.thenexusreborn.api.storage.objects.SqlCodec;
import com.thenexusreborn.survivalgames.SurvivalGames;

import java.util.Objects;

public abstract class Setting implements Cloneable {
    private long id;
    
    @ColumnInfo(name = "name", type = "varchar(100)", codec = InfoCodec.class)
    private Info info;
    private String category;
    @ColumnInfo(type = "varchar(1000)", codec = ValueCodec.class)
    private Value value;
    
    protected Setting() {}
    
    public Setting(Info info, String category, Value value) {
        this.info = info;
        this.category = category;
        this.value = value;
    }
    
    public void setValue(Value value) {
        this.value = value;
    }
    
    public String getCategory() {
        return category;
    }
    
    public long getId() {
        return id;
    }
    
    public Info getInfo() {
        return info;
    }
    
    public Value getValue() {
        return value;
    }
    
    @Override
    public Setting clone() {
        Setting clone;
        try {
            clone = (Setting) super.clone();
            clone.id = 0;
            clone.value = this.value.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    @TableInfo("sgsettinginfo")
    public static class Info {
        private long id;
        private String name, displayName, description, type;
        @ColumnInfo(type = "varchar(1000)", codec = ValueCodec.class)
        private Value defaultValue, minValue, maxvalue;
        
        private Info() {}
    
        public Info(String name, String displayName, String description, String type, Value defaultValue) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
            this.defaultValue = defaultValue;
            this.type = type;
        }
    
        public Info(String name, String displayName, String description, String type, Value defaultValue, Value minValue, Value maxvalue) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
            this.defaultValue = defaultValue;
            this.minValue = minValue;
            this.maxvalue = maxvalue;
            this.type = type;
        }
    
        public long getId() {
            return id;
        }
    
        public String getName() {
            return name;
        }
    
        public String getDisplayName() {
            return displayName;
        }
    
        public String getDescription() {
            return description;
        }
    
        public Value getDefaultValue() {
            return defaultValue;
        }
    
        public Value getMinValue() {
            return minValue;
        }
    
        public Value getMaxvalue() {
            return maxvalue;
        }
    
        public String getType() {
            return type;
        }
    
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Info info = (Info) o;
            return Objects.equals(type + "_" + name, info.type + "_" + info.name);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(type + "_" + name);
        }
    }
    
    public static class InfoCodec implements SqlCodec<Info> {
        public String encode(Object object) {
            return ((Info) object).getName();
        }
    
        public Info decode(String encoded) {
            return SurvivalGames.getPlugin(SurvivalGames.class).getSettingRegistry().get(encoded);
        }
    }
}