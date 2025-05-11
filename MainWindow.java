package view;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Battlefield;
import model.MyTableModelMain;
import repository.DBWorker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class MainWindow extends JFrame {
    private Battlefield field = new Battlefield();
    private JTable jTable;//отображает данные, но не хранит
    private MyTableModelMain myTableModelMain;
    private JMenuBar menuBar;
    private JMenu menuFile, menuAbout, menuApp;
    private JMenuItem menuItemAbout1, menuItemAbout2;;
    private JMenuItem menuItemFile1, menuItemFile2;
    private JTextField NameTankField, HPTankField, AbilityTankField;
    private JButton buttonHeavyTank, buttonLightTank;
    private JButton buttonOk;
    private JMenuItem menuItemApp1,menuItemApp2, menuItemApp3;
    private boolean hasErrors = false;
    private JDialog jDialogChoiceType, jDialogAddTank;
    private JPanel JPanelButtons;
    private JCheckBoxMenuItem menuItemFile3;
    private JButton addTankButton, deleteTankButton, abilityTankButton, saveDataButton, readDataButton, exitButton;
    private JCheckBox editModeCheckBox;
    private JMenuItem menuItemFile4;
    private JFileChooser fileChooser;
    private boolean hasUnsavedChanges = false;
    private int numNewTanks=0;
    private int numDeleteTanks=0;
    private int id= 6;

    public MainWindow(Battlefield battlefield){
        super("Наши танки");
        this.field = battlefield;
        madeMenu();
        myTableModelMain = new MyTableModelMain(field); // Создаем модель
        jTable = new JTable();
        jTable.setModel(myTableModelMain);
        jTable.getModel().addTableModelListener(e -> {
            if (isEditModeEnabled()) {
                hasUnsavedChanges = true;
            }
        });
        JScrollPane jScrollPane = new JScrollPane(jTable);
        JPanelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        addButtons();
        this.add(JPanelButtons, BorderLayout.SOUTH);
        this.add(jScrollPane);//крутить таблицу
        this.pack();
        this.setSize(950,600);
        SetFont();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                menuItemFile4.doClick();
            }
        });
        this.setLocationRelativeTo(null);//расположение посередине
        this.setVisible(true);//чтобы выводилось
    }

    private void addButtons() {
        addTankButton = new JButton("Добавить танк");
        addTankButton.addActionListener(e -> {
            menuItemApp2.doClick();
        });
        JPanelButtons.add(addTankButton);

        deleteTankButton = new JButton("Удалить танк");
        deleteTankButton.addActionListener(e -> {
            menuItemApp1.doClick();
        });
        JPanelButtons.add(deleteTankButton);

        abilityTankButton = new JButton("Использовать возможность");
        abilityTankButton.addActionListener(e -> {
            menuItemApp3.doClick();
        });
        JPanelButtons.add(abilityTankButton);

        saveDataButton = new JButton("Сохранить");
        saveDataButton.addActionListener(e -> {
            menuItemFile2.doClick();
        });
        JPanelButtons.add(saveDataButton);

        readDataButton = new JButton("Загрузить");
        readDataButton.addActionListener(e -> {
            menuItemFile1.doClick();
        });
        JPanelButtons.add(readDataButton);

        exitButton = new JButton("Выход");
        exitButton.addActionListener(e -> {
            menuItemFile4.doClick();
        });
        JPanelButtons.add(exitButton);

        editModeCheckBox = new JCheckBox("Режим редактирования");
        editModeCheckBox.addActionListener(e -> {
            menuItemFile3.doClick();
        });
        JPanelButtons.add(editModeCheckBox);
    }
    private void ViewErrorDialog(){
        JDialog errorDialog = new JDialog(MainWindow.this, "Ошибка", true);
        errorDialog.setSize(300, 100);
        errorDialog.setLocationRelativeTo(MainWindow.this);

        // Добавляем сообщение об ошибке
        JPanel messagePanelError = new JPanel();
        messagePanelError.add(new JLabel("Выполнение невозможно. Выделите танк."));
        errorDialog.add(messagePanelError);

        // Добавляем кнопку OK
        JPanel buttonPanelError = new JPanel();
        JButton buttonOk = new JButton("OK");
        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                errorDialog.dispose();//закрываем окно ошибки
            }
        });
        buttonPanelError.add(buttonOk);
        errorDialog.add(buttonPanelError, BorderLayout.SOUTH);
        errorDialog.setVisible(true);
    }
    private void SetFont(){
        jTable.setFont(new Font("Helvetica", Font.PLAIN | Font.ITALIC, 12));
    }
    private void madeMenu(){
        menuBar = new JMenuBar();
        menuFile = new JMenu("Файл");
        menuItemFile1 = new JMenuItem("Открыть");
        menuItemFile2 = new JMenuItem("Сохранить");
        menuItemFile3 = new JCheckBoxMenuItem("Изменить");
        menuItemFile4 = new JMenuItem("Выход");

        menuApp = new JMenu("Работа с данными");
        menuItemApp1 = new JMenuItem("Удалить танк");
        menuItemApp2 = new JMenuItem("Добавить танк");
        menuItemApp3 = new JMenuItem("Возможность танка");

        menuAbout = new JMenu("Для вас");
        menuItemAbout1 = new JMenuItem("О приложении");
        menuItemAbout2 = new JMenuItem("Как пользоваться");

        setMenuUse();
        menuApp.add(menuItemApp1);
        menuApp.add(menuItemApp2);
        menuApp.add(menuItemApp3);
        menuFile.add(menuItemFile1);
        menuFile.add(menuItemFile2);
        menuFile.add(menuItemFile3);
        menuFile.add(menuItemFile4);
        menuAbout.add(menuItemAbout1);
        menuAbout.add(menuItemAbout2);
        menuBar.add(menuFile);
        menuBar.add(menuApp);
        menuBar.add(menuAbout);
        this.setJMenuBar(menuBar);
    }
    private boolean isEditModeEnabled() {
        return menuItemFile3 != null && menuItemFile3.isSelected();
    }
    private void setMenuUse() {
        menuItemFile4.addActionListener(e -> {
            if (hasUnsavedChanges || numNewTanks !=0 || numDeleteTanks!=0) {
                int choice = JOptionPane.showConfirmDialog(
                        MainWindow.this,
                        "У вас есть несохраненные изменения. Сохранить перед выходом?",
                        "Подтверждение",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    saveFile();
                    hasUnsavedChanges = false;
                } else if (choice == JOptionPane.CANCEL_OPTION) {
                    // Отменяем выход из режима редактирования
                    menuItemFile3.setSelected(true);
                    editModeCheckBox.setSelected(true);
                    return;
                } else {
                    hasUnsavedChanges = false;
                }
            }
            int confirm = JOptionPane.showConfirmDialog(
                    MainWindow.this,
                    "Вы действительно хотите выйти?",
                    "Подтверждение выхода",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        menuItemFile3.addActionListener(e -> {
            boolean isSelected = menuItemFile3.isSelected();
            if (!isSelected && hasUnsavedChanges) {
                int choice = JOptionPane.showConfirmDialog(
                        MainWindow.this,
                        "У вас есть несохраненные изменения. Сохранить перед выходом?",
                        "Подтверждение",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    saveFile();
                    hasUnsavedChanges = false;
                } else if (choice == JOptionPane.CANCEL_OPTION) {
                    // Отменяем выход из режима редактирования
                    menuItemFile3.setSelected(true);
                    editModeCheckBox.setSelected(true);
                    return;
                } else {
                    hasUnsavedChanges = false;
                }
            }
            myTableModelMain.setEditMode(isSelected);
            editModeCheckBox.setSelected(isSelected);
            if (isSelected) {
                JOptionPane.showMessageDialog(this,
                        "Режим редактирования включен",
                        "Информация",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menuItemFile2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    saveFile();
                }
        });
        menuItemFile1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        menuItemAbout1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog aboutApplication = new JDialog(MainWindow.this, "О приложении", true);
                aboutApplication.setLocationRelativeTo(MainWindow.this);
                aboutApplication.setSize(200, 200);

                JPanel panelMessageAboutApp = new JPanel();
                panelMessageAboutApp.add(new JLabel(
                        "<html><div style='width: 100px;'>" +
                                "Это приложение было разработано для " +
                                "выполнения пятой лабораторной работы " +
                                "по Объектно-Ориентированному " +
                                "Программированию.</div></html>"
                ));
                aboutApplication.add(panelMessageAboutApp);
                aboutApplication.setVisible(true);
            }
        });
        menuItemAbout2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog aboutApplication = new JDialog(MainWindow.this, "Советы для пользователя", true);
                aboutApplication.setLocationRelativeTo(MainWindow.this);
                aboutApplication.setSize(450, 320);

                JPanel panelMessageAboutApp = new JPanel();
                panelMessageAboutApp.add(new JLabel(
                        "<html><div style='width: 100%; text-align: left;'>" +
                                "<b>Инструкция по использованию:</b><br><br>" +
                                "1. <b>Добавление танка:</b><br>" +
                                "&nbsp;&nbsp;- Откройте меню <b>\"Работа с данными\"</b><br>" +
                                "&nbsp;&nbsp;- Выберите пункт <b>\"Добавить танк\"</b><br>" +
                                "&nbsp;&nbsp;- Выберите танк, который хотите добавить</b><br>" +
                                "&nbsp;&nbsp;- Заполните поля и нажмите <b>\"ОК\"</b><br>" +
                                "2. <b>Удаление танка:</b><br>" +
                                "&nbsp;&nbsp;- Выберите танк в списке<br>" +
                                "&nbsp;&nbsp;- Откройте меню и нажмите на пункт <b>\"Удалить танк\"</b><br>" +
                                "3. <b>Редактирование полей:</b><br>" +
                                "&nbsp;&nbsp;- Кликните два раза на поле в таблице, которое хотите поменять<br>" +
                                "&nbsp;&nbsp;- После замены данных нажмите <b>\"Enter\"</b><br>" +
                                "4. <b>Сохранение и загрузка:</b><br>" +
                                "&nbsp;&nbsp;- Для сохранения данных выберите <b>\"Файл → Сохранить\"</b><br>" +
                                "&nbsp;&nbsp;- Для загрузки данных выберите <b>\"Файл → Загрузить\"</b>" +
                                "</div></html>"
                ));
                aboutApplication.add(panelMessageAboutApp);
                aboutApplication.setVisible(true);
            }
        });
        removeTank();
        addTank();
        useAbility();
    }
    private void saveDataToFile(File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(field);
            // Сохраняем также в БД
            DBWorker.saveBattlefield(field);
            JOptionPane.showMessageDialog(
                    MainWindow.this,
                    "Данные успешно сохранены в файл и БД: " + file.getName(),
                    "Сохранение завершено",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException ex) {
            // обработка ошибок
        }
    }
    private void openFile() {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Открытие файла данных");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Файлы данных (*.dat)", "dat"));

        int userSelection = fileChooser.showOpenDialog(MainWindow.this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile))) {
                // 1. Сначала загружаем данные из файла
                Battlefield loadedField = (Battlefield) ois.readObject();

                // 2. Обновляем UI
                this.field = loadedField;
                this.myTableModelMain = new MyTableModelMain(loadedField);
                jTable.setModel(myTableModelMain);

                // 3. Сохраняем в БД (теперь данные гарантированно есть)
                DBWorker.saveBattlefield(loadedField);

                JOptionPane.showMessageDialog(
                        MainWindow.this,
                        "Данные успешно загружены из файла и сохранены в БД: " + selectedFile.getName(),
                        "Загрузка завершена",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(
                        MainWindow.this,
                        "Ошибка при загрузке файла: " + ex.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }
        }
    }
    private void saveFile() {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить файл данных");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Файлы данных (*.dat)", "dat"));
        fileChooser.setSelectedFile(new File("tanks.dat"));
        int userSelection = fileChooser.showSaveDialog(MainWindow.this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".dat")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".dat");
            }
            if (fileToSave.exists()) {
                int confirm = JOptionPane.showConfirmDialog(
                        MainWindow.this,
                        "Файл уже существует. Перезаписать?",
                        "Подтверждение",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            saveDataToFile(fileToSave);
            hasUnsavedChanges = false;
        }
    }
    private void removeTank(){
        menuItemApp1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    myTableModelMain.deleteTank(jTable.getSelectedRow());
                    numDeleteTanks+=1;
                }catch(IndexOutOfBoundsException ex){
                    ViewErrorDialog();
                }
            }
        });
    }
    private void addTank(){
        menuItemApp2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialogChoiceType = new JDialog(MainWindow.this,"Выбор танка", true);
                jDialogChoiceType.setSize(300, 100);
                jDialogChoiceType.setLocationRelativeTo(MainWindow.this);
                buttonHeavyTank = new JButton("Тяжёлый танк");
                buttonLightTank = new JButton("Лёгкий танк");
                JPanel messageChoice = new JPanel();
                JPanel buttonPanelChoice = new JPanel();
                messageChoice.add(new JLabel("Какой танк вы хотите добавить?"));
                jDialogAddTank = new JDialog(MainWindow.this,"Добавление танка", true);
                jDialogAddTank.setSize(500,250);
                jDialogAddTank.setLocationRelativeTo(MainWindow.this);
                addHeavyTank();
                addLightTank();

                buttonPanelChoice.add(buttonHeavyTank);
                buttonPanelChoice.add(buttonLightTank);
                jDialogChoiceType.add(messageChoice, BorderLayout.CENTER);
                jDialogChoiceType.add(buttonPanelChoice, BorderLayout.SOUTH);
                jDialogChoiceType.setVisible(true);
            }
        });
    }
    private void addLightTank(){
        buttonLightTank.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialogChoiceType.dispose();

                JPanel jPanelInputNameTank = new JPanel();
                jPanelInputNameTank.add(new JLabel("Введите название танка: "));
                NameTankField = new JTextField(20);
                jPanelInputNameTank.add(NameTankField);

                JPanel jPanelInputHPTank = new JPanel();
                jPanelInputHPTank.add(new JLabel("Введите кол-во здоровья танка: "));
                HPTankField = new JTextField(20);
                jPanelInputHPTank.add(HPTankField);

                JPanel jPanelInputAbilityTank = new JPanel();
                jPanelInputAbilityTank.add(new JLabel("Введите дальность обзора танка: "));
                AbilityTankField = new JTextField(20);
                jPanelInputAbilityTank.add(AbilityTankField);

                JPanel generalPanelAdd = new JPanel();
                generalPanelAdd.setLayout(new BoxLayout(generalPanelAdd, BoxLayout.Y_AXIS));
                generalPanelAdd.add(jPanelInputNameTank);
                generalPanelAdd.add(jPanelInputHPTank);
                generalPanelAdd.add(jPanelInputAbilityTank);

                buttonOk = new JButton("ОК");
                buttonOk.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        checkFields();
                        if (hasErrors) {
                            return;
                        }

                        try {
                            String name = NameTankField.getText().trim();
                            int hp = Integer.parseInt(HPTankField.getText().trim());
                            int ability = Integer.parseInt(AbilityTankField.getText().trim());
                            myTableModelMain.addLightTank(id+=1, name, hp, ability);
                            numNewTanks +=1;
                            jDialogAddTank.dispose();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(jDialogAddTank,
                                    "Ошибка: " + ex.getMessage(),
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                generalPanelAdd.add(buttonOk);
                jDialogAddTank.add(generalPanelAdd);
                jDialogAddTank.setVisible(true);
            }
        });
    }
    private void addHeavyTank(){
        buttonHeavyTank.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialogChoiceType.dispose();

                JPanel jPanelInputNameTank = new JPanel();
                jPanelInputNameTank.add(new JLabel("Введите название танка: "));
                NameTankField = new JTextField(20);
                jPanelInputNameTank.add(NameTankField);

                JPanel jPanelInputHPTank = new JPanel();
                jPanelInputHPTank.add(new JLabel("Введите кол-во здоровья танка: "));
                HPTankField = new JTextField(20);
                jPanelInputHPTank.add(HPTankField);

                JPanel jPanelInputAbilityTank = new JPanel();
                jPanelInputAbilityTank.add(new JLabel("Введите толщину брони танка: "));
                AbilityTankField = new JTextField(20);
                jPanelInputAbilityTank.add(AbilityTankField);

                JPanel generalPanelAdd = new JPanel();
                generalPanelAdd.setLayout(new BoxLayout(generalPanelAdd, BoxLayout.Y_AXIS));
                generalPanelAdd.add(jPanelInputNameTank);
                generalPanelAdd.add(jPanelInputHPTank);
                generalPanelAdd.add(jPanelInputAbilityTank);

                buttonOk = new JButton("ОК");
                buttonOk.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        checkFields();
                        if (hasErrors) {
                            return;
                        }
                        try {
                            String name = NameTankField.getText().trim();
                            int hp = Integer.parseInt(HPTankField.getText().trim());
                            int ability = Integer.parseInt(AbilityTankField.getText().trim());
                            myTableModelMain.addHeavyTank(id+=1, name, hp, ability);
                            numNewTanks +=1;
                            jDialogAddTank.dispose();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(jDialogAddTank,
                                    "Ошибка: " + ex.getMessage(),
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                generalPanelAdd.add(buttonOk);
                jDialogAddTank.add(generalPanelAdd);
                jDialogAddTank.setVisible(true);
            }
        });
    }
    private void checkFields() {
        NameTankField.setBackground(Color.WHITE);
        HPTankField.setBackground(Color.WHITE);
        AbilityTankField.setBackground(Color.WHITE);

        hasErrors = false;

        if (NameTankField.getText().trim().isEmpty()) {
            NameTankField.setBackground(Color.RED);
            hasErrors = true;
        }

        try {
            String hpText = HPTankField.getText().trim();
            if (hpText.isEmpty()) {
                HPTankField.setBackground(Color.RED);
                hasErrors = true;
            } else {
                int hp = Integer.parseInt(hpText);
                if (hp <= 0) {
                    HPTankField.setBackground(Color.RED);
                    hasErrors = true;
                }
            }
        } catch (NumberFormatException ex) {
            HPTankField.setBackground(Color.RED);
            hasErrors = true;
        }

        try {
            String abilityText = AbilityTankField.getText().trim();
            if (abilityText.isEmpty()) {
                AbilityTankField.setBackground(Color.RED);
                hasErrors = true;
            } else {
                int ability = Integer.parseInt(abilityText);
                if (ability <= 0) {
                    AbilityTankField.setBackground(Color.RED);
                    hasErrors = true;
                }
            }
        } catch (NumberFormatException ex) {
            AbilityTankField.setBackground(Color.RED);
            hasErrors = true;
        }

        if (hasErrors) {
            String message = "";
            if (NameTankField.getText().trim().isEmpty()) {
                message = "Название танка не может быть пустым!\n";
            }
            if (HPTankField.getBackground() == Color.RED) {
                message += "Здоровье должно быть положительным числом!\n";
            }
            if (AbilityTankField.getBackground() == Color.RED) {
                message += (buttonHeavyTank.isEnabled() ?
                        "Толщина брони" : "Дальность обзора") + " должна быть положительным числом!";
            }
            JOptionPane.showMessageDialog(jDialogAddTank,
                    message,
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void useAbility(){
       menuItemApp3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = jTable.getSelectedRow();
                if (selectedRow == -1) { // Проверка, выбрана ли строка
                    ViewErrorDialog();
                } else {
                    try {

                        JDialog jDialog = new JDialog(MainWindow.this,"Результаты использования возможностей", true);
                        jDialog.setSize(400, 150);
                        jDialog.setLocationRelativeTo(MainWindow.this);
                        JPanel messagePanelDelete = new JPanel();
                        String abilityResult = field.performAbilityTank(jTable.getSelectedRow());
                        if (abilityResult.contains(" никого не нашёл!") || abilityResult.contains(" получил пробитие!")) {
                            ImageIcon defeatIcon = new ImageIcon("defeat.jpg");
                            JLabel defeatLabel = new JLabel(defeatIcon);
                            messagePanelDelete.add(defeatLabel);
                            messagePanelDelete.add(new JLabel(abilityResult));
                        } else {
                            if (abilityResult.contains(" обнаружил врага!") || abilityResult.contains(" получил непробитие!")){
                                ImageIcon defeatIcon = new ImageIcon("victory1.jpg");
                                JLabel victoryLabel = new JLabel(defeatIcon);
                                messagePanelDelete.add(victoryLabel);
                                messagePanelDelete.add(new JLabel(abilityResult));
                            }
                        }
                        jDialog.add(messagePanelDelete, BorderLayout.CENTER);
                        jDialog.setVisible(true);
                    }catch(ArrayIndexOutOfBoundsException ex){
                        ViewErrorDialog();
                    }
                }
            }
        });
    }
}
