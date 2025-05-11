package model;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class MyTableModelMain extends AbstractTableModel {
    private Battlefield data;
    private boolean editMode = false;

    public MyTableModelMain(Battlefield field) {
        this.data = field;
    }

    public void setEditMode(boolean enabled) {
        this.editMode = enabled;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editMode;
    }

    @Override
    public int getRowCount() {
        return data.getCount();
    }

    public Tank getTank(int index) {
        return data.getTank(index);
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Название";
            case 1: return "Прочность";
            case 2: return "Дальность обзора";
            case 3: return "Толщина брони";
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return String.class;
            default:
                return Integer.class;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Tank tank = data.getTank(rowIndex);
        try {
            switch (columnIndex) {
                case 0:
                    // Проверка на пустое название
                    String newName = ((String) aValue).trim();
                    if (newName.isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                                "Название танка не может быть пустым!",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    tank.setName(newName);
                    break;
                case 1:
                    int hp = Integer.parseInt(aValue.toString());
                    if (hp <= 0) {
                        throw new NumberFormatException("Прочность должна быть положительным числом");
                    }
                    tank.setHPTank(hp);
                    break;
                case 2:
                    if (tank instanceof LightTank) {
                        int viewRange = Integer.parseInt(aValue.toString());
                        if (viewRange <= 0) {
                            throw new NumberFormatException("Дальность обзора должна быть положительным числом");
                        }
                        ((LightTank) tank).setViewRange(viewRange);
                    }
                    break;
                case 3:
                    if (tank instanceof HeavyTank) {
                        int armor = Integer.parseInt(aValue.toString());
                        if (armor <= 0) {
                            throw new NumberFormatException("Толщина брони должна быть положительным числом");
                        }
                        ((HeavyTank) tank).setArmorThickness(armor);
                    }
                    break;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return data.getTank(rowIndex).getName();
            case 1: return data.getTank(rowIndex).getHPTank();
            case 2: {
                Tank tank = data.getTank(rowIndex);
                if(tank instanceof LightTank) {
                    return ((LightTank) tank).getViewRange();
                } else return "-";
            }
            case 3: {
                Tank tank = data.getTank(rowIndex);
                if (tank instanceof HeavyTank) {
                    return ((HeavyTank) tank).getArmorThickness();
                } else return "-";
            }
        }
        return "default";
    }

    public void deleteTank(int index) {
        this.data.remove(index);
        fireTableDataChanged();
    }

    public void addLightTank(int id, String name, int HP, int viewRange) {
        data.addTank(new LightTank(id, name, HP, viewRange));
        fireTableDataChanged();
    }

    public void addHeavyTank(int id, String name, int HP, int armorThickness) {
        data.addTank(new HeavyTank(id, name, HP, armorThickness));
        fireTableDataChanged();
    }
}