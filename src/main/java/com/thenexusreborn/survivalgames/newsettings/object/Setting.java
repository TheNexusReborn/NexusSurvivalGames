package com.thenexusreborn.survivalgames.newsettings.object;

import com.thenexusreborn.api.frameworks.value.Value;
import com.thenexusreborn.api.storage.annotations.ColumnInfo;
import com.thenexusreborn.api.storage.objects.SqlCodec;
import com.thenexusreborn.survivalgames.SurvivalGames;

import java.util.Objects;

public abstract class Setting {
    private long id;
    
    @ColumnInfo(name = "name", type = "varchar(100)", codec = InfoCodec.class)
    private Info info;
    private String category;
    private Value value;
    
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
    
    public static class Info {
        private long id;
        private String name, displayName, description, type;
        private Value defaultValue;
    
        public Info(String name, String displayName, String description, String type, Value defaultValue) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
            this.defaultValue = defaultValue;
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
    
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Info info = (Info) o;
            return Objects.equals(name, info.name);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(name);
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