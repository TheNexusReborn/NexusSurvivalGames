package com.thenexusreborn.survivalgames.settings.object;

import com.stardevllc.starlib.Value;
import com.thenexusreborn.survivalgames.SurvivalGames;
import me.firestar311.starsql.api.annotations.column.ColumnCodec;
import me.firestar311.starsql.api.annotations.column.ColumnIgnored;
import me.firestar311.starsql.api.annotations.column.ColumnName;
import me.firestar311.starsql.api.annotations.column.ColumnType;
import me.firestar311.starsql.api.annotations.table.TableName;
import me.firestar311.starsql.api.objects.SqlCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Setting implements Cloneable {
    protected long id;
    
    @ColumnName("name")
    @ColumnType("varchar(100)")
    @ColumnCodec(InfoCodec.class)
    protected Info info;
    protected String category;
    @ColumnType("varchar(1000)")
    protected Value value;

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
    
    public void setValue(Object value) {
        if (!this.info.changeListeners.isEmpty()) {
            for (ChangeListener changeListener : this.info.changeListeners) {
                changeListener.onChange(this, this.value.getType(), this.value.get(), value);
            }
        }
        this.value.set(value);
    }
    
    public Value getValue() {
        if (this.value == null || this.value.get() == null) {
            return this.info.getDefaultValue();
        }
        return value.clone();
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

    @Override
    public String toString() {
        return "Setting{" +
                "id=" + id +
                ", info=" + info.name +
                ", category='" + category + '\'' +
                ", value=" + value +
                '}';
    }

    @TableName("sgsettinginfo")
    public static class Info {
        private long id;
        private String name, displayName, description, type;
        @ColumnType("varchar(1000)")
        private Value defaultValue, minValue, maxValue;
        @ColumnIgnored
        protected List<ChangeListener> changeListeners = new ArrayList<>();
        
        private Info() {}
    
        public Info(String name, String displayName, String description, String type, Value defaultValue) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
            this.defaultValue = defaultValue;
            this.type = type;
        }
    
        public Info(String name, String displayName, String description, String type, Value defaultValue, Value minValue, Value maxValue) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
            this.defaultValue = defaultValue;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.type = type;
        }

        public void addChangeListener(ChangeListener listener) {
            this.changeListeners.add(listener);
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
    
        public Value getMaxValue() {
            return maxValue;
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

        @Override
        public String toString() {
            return "Info{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", defaultValue=" + defaultValue +
                    ", minValue=" + minValue +
                    ", maxValue=" + maxValue +
                    '}';
        }
    }
    
    public static class InfoCodec implements SqlCodec<Info> {
        public String encode(Object object) {
            Info info = (Info) object;
            return info.getType() + ":" + info.getName();
        }
    
        public Info decode(String encoded) {
            String[] split = encoded.split(":");
            SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
            return switch (split[0]) {
                case "lobby" -> plugin.getLobbySettingRegistry().get(split[1]);
                case "game" -> plugin.getGameSettingRegistry().get(split[1]);
                default -> null;
            };
        }
    }
}