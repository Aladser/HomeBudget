package homebudget.frames;

import homebudget.views.TsctTableCellRender;
import homebudget.models.OperationsTableLine;
import homebudget.models.TransactionsTableModel;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.table.JTableHeader;

public class TrsctFrame extends javax.swing.JFrame {
    homebudget.HomeBudget launcher;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    GregorianCalendar startDateCldr;
    GregorianCalendar finalDateCldr;
    /** Дата первой записи */
    final GregorianCalendar FIRST_DATE_RECORD; 
    /** Сегодняшняя дата */
    final GregorianCalendar LAST_DATE_RECORD;
    /** Вручную введенные даты */
    boolean isManualData = false;
    
    public TrsctFrame(homebudget.HomeBudget launcher) throws SQLException, AWTException {
        this.launcher = launcher;
        // инициализация крайних дат
        FIRST_DATE_RECORD = new GregorianCalendar();
        FIRST_DATE_RECORD.setTime(launcher.TRSCTS.getFirstRecordDate());
        LAST_DATE_RECORD = new GregorianCalendar(); 
        LAST_DATE_RECORD.setTimeInMillis(new Date().getTime()+86400000);
        // рендеринг окна
        initComponents();
        getContentPane().setBackground(Color.white);
        setIconImage( new ImageIcon( getClass().getResource("images/logo.png") ).getImage() );
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth())/2, (screenSize.height - getHeight())/2);
        tableScrollPane.setOpaque(true);
        tableScrollPane.getViewport().setBackground(Color.white);
        updateComboBox(typeComboBox.getSelectedIndex());
        // background элементов
        oprtComboBox.setBackground(Color.white);
        typeComboBox.setBackground(Color.white);
        timeGapPrdBox.setBackground(Color.white);
        // иконки кнопок
        addBtn.setIcon( new ImageIcon(getClass().getResource("images/addIcon.png")) );
        delBaseBtn.setIcon( new ImageIcon(getClass().getResource("images/returnIcon.png")) );
        // рендер таблицы
        table.setRowHeight(30);
        JTableHeader tableHeader = table.getTableHeader();  
        tableHeader.setFont(new java.awt.Font("Times New Roman", 1, 14));
        tableHeader.setBackground(new java.awt.Color(240,240,240));
        Font digitalFont = launcher.DIGITAL_FONT.deriveFont( Font.PLAIN, 40 );
        // баланс
        balanceFld.setFont(digitalFont);
        digitalFont = digitalFont.deriveFont(Font.PLAIN, 20);
        balanceLbl.setFont(digitalFont);
        incValLbl.setFont(digitalFont);
        expValLbl.setFont(digitalFont);
        // ограничение выбора последней даты
        finalDateChooserBox.setMaxDate(LAST_DATE_RECORD);
        startDateChooserBox.setMaxDate(LAST_DATE_RECORD);
        updateData();
    }
   
    // модель choiceOprtComboBox
    public void updateComboBox(int type){
        ArrayList<OperationsTableLine> data = launcher.OPRTS.getData(typeComboBox.getSelectedIndex());
        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>();
        for(int i=0; i<data.size(); i++) cbModel.addElement(data.get(i).name);
        oprtComboBox.setModel(cbModel);        
    }
    
    // управление данными окна
    private void updateData() throws SQLException{
        int choicePar = timeGapPrdBox.getSelectedIndex();
        // есть вручную введенные даты
        if(isManualData){
            table.setModel( new TransactionsTableModel(launcher.TRSCTS.getData(startDateCldr, finalDateCldr)));
            incValLbl.setText(Double.toString(launcher.TRSCTS.getTotalIncome(startDateCldr, finalDateCldr))+" Р");
            expValLbl.setText(Double.toString(launcher.TRSCTS.getTotalExpense(startDateCldr, finalDateCldr))+" Р");
            table.getColumnModel().getColumn(0).setCellRenderer( new TsctTableCellRender() );
            table.getColumnModel().getColumn(1).setCellRenderer( new TsctTableCellRender() );
            isManualData = false;
            return;
        }
        showDataBtn.setEnabled(false);
        // выбор ручного ввода даты
        if(choicePar == 3 && !isManualData){
            startDateChooserBox.setMinDate(FIRST_DATE_RECORD);
            finalDateChooserBox.setMinDate(startDateChooserBox.getSelectedDate());
            startDateChooserBox.setMaxDate(LAST_DATE_RECORD);
            showDataBtn.setEnabled(true);
            lockDateChoice(false);
            return;
        }
        // убрать ограничение стартовой даты для других choicePar
       else{
           GregorianCalendar cldr = new GregorianCalendar();
           cldr.clear();
           startDateChooserBox.setMinDate(cldr);
       }
        // дата начала периода
        GregorianCalendar startDate = new GregorianCalendar();
        if(choicePar==1) startDate.set(Calendar.DAY_OF_MONTH, 1);
        if(choicePar==2) startDate.setTime(launcher.TRSCTS.getFirstRecordDate());
        startDate = homebudget.HomeBudget.setHourZero(startDate);
        // обновление DateComboBox'ов с учетом блокировки элемента
        lockDateChoice(false);
        startDateChooserBox.setSelectedDate(startDate);
        lockDateChoice(true);
        // рендер таблицы
        if( choicePar == 2 ){
            table.setModel( new TransactionsTableModel(launcher.TRSCTS.getData()));
            incValLbl.setText(Double.toString(launcher.TRSCTS.getTotalIncome())+" Р");
            expValLbl.setText(Double.toString(launcher.TRSCTS.getTotalExpense())+" Р");
        }
        else{
            table.setModel( new TransactionsTableModel(launcher.TRSCTS.getData(startDate, LAST_DATE_RECORD)));
            incValLbl.setText(Double.toString(launcher.TRSCTS.getTotalIncome(startDate, LAST_DATE_RECORD))+" Р");
            expValLbl.setText(Double.toString(launcher.TRSCTS.getTotalExpense(startDate, LAST_DATE_RECORD))+" Р");
        }
        table.getColumnModel().getColumn(0).setCellRenderer( new TsctTableCellRender() );
        table.getColumnModel().getColumn(1).setCellRenderer( new TsctTableCellRender() );
        // баланс
        balanceFld.setText(Double.toString(launcher.TRSCTS.getBalance())+" P");
    }
    
    // управляет блокировкой выбора даты
    private void lockDateChoice(boolean par){
        startDateChooserBox.setLocked(par);
        finalDateChooserBox.setLocked(par);        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableScrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        topPanel = new javax.swing.JPanel();
        balanceLbl = new javax.swing.JLabel();
        balanceFld = new javax.swing.JLabel();
        inputSumFld = new javax.swing.JTextField();
        typeComboBox = new javax.swing.JComboBox<>();
        oprtComboBox = new javax.swing.JComboBox<>();
        addBtn = new javax.swing.JButton();
        delBaseBtn = new javax.swing.JButton();
        datePanel = new javax.swing.JPanel();
        startDateChooserBox = new datechooser.beans.DateChooserCombo();
        finalDateChooserBox = new datechooser.beans.DateChooserCombo();
        timeGapPrdBox = new javax.swing.JComboBox<>();
        showDataBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        incValLbl = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        expValLbl = new javax.swing.JLabel();
        opertsBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Домашний бюджет 2.104");
        setBackground(new java.awt.Color(255, 255, 255));

        tableScrollPane.setBackground(new java.awt.Color(255, 255, 255));

        tableScrollPane.setViewportView(table);

        topPanel.setBackground(new java.awt.Color(255, 255, 255));
        topPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        balanceLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        balanceLbl.setText("Баланс");

        balanceFld.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        inputSumFld.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inputSumFldMouseClicked(evt);
            }
        });
        inputSumFld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputSumFldKeyReleased(evt);
            }
        });

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Доход", "Расход" }));
        typeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeComboBoxActionPerformed(evt);
            }
        });

        addBtn.setBackground(new java.awt.Color(255, 255, 255));
        addBtn.setToolTipText("Добавить");
        addBtn.setEnabled(false);
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        delBaseBtn.setBackground(new java.awt.Color(255, 255, 255));
        delBaseBtn.setToolTipText("Отменить последнюю операцию");
        delBaseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delBaseBtnActionPerformed(evt);
            }
        });

        datePanel.setBackground(new java.awt.Color(255, 255, 255));
        datePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Время", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        datePanel.setToolTipText("");

        startDateChooserBox.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
        startDateChooserBox.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
            new datechooser.view.appearance.ViewAppearance("custom",
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    true,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(0, 0, 255),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(128, 128, 128),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.LabelPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.LabelPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(255, 0, 0),
                    false,
                    false,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                (datechooser.view.BackRenderer)null,
                false,
                true)));
    startDateChooserBox.setLocked(true);
    startDateChooserBox.setNothingAllowed(false);
    startDateChooserBox.setCalendarBackground(new java.awt.Color(255, 255, 255));
    startDateChooserBox.setFieldFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
    startDateChooserBox.setNavigateFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));

    finalDateChooserBox.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
    finalDateChooserBox.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
        new datechooser.view.appearance.ViewAppearance("custom",
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                new java.awt.Color(0, 0, 0),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                new java.awt.Color(0, 0, 0),
                new java.awt.Color(0, 0, 255),
                true,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                new java.awt.Color(0, 0, 255),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                new java.awt.Color(128, 128, 128),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.LabelPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                new java.awt.Color(0, 0, 0),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.LabelPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 13),
                new java.awt.Color(0, 0, 0),
                new java.awt.Color(255, 0, 0),
                false,
                false,
                new datechooser.view.appearance.swing.ButtonPainter()),
            (datechooser.view.BackRenderer)null,
            false,
            true)));
finalDateChooserBox.setLocked(true);
finalDateChooserBox.setNothingAllowed(false);
finalDateChooserBox.setFormat(0);
finalDateChooserBox.setCalendarBackground(new java.awt.Color(255, 255, 255));
finalDateChooserBox.setFieldFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
finalDateChooserBox.setNavigateFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));

timeGapPrdBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Последний день", "Последний месяц", "Все время", "Вручную" }));
timeGapPrdBox.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        timeGapPrdBoxActionPerformed(evt);
    }
    });

    showDataBtn.setBackground(new java.awt.Color(255, 255, 255));
    showDataBtn.setText("Показать");
    showDataBtn.setEnabled(false);
    showDataBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            showDataBtnActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout datePanelLayout = new javax.swing.GroupLayout(datePanel);
    datePanel.setLayout(datePanelLayout);
    datePanelLayout.setHorizontalGroup(
        datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(datePanelLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(startDateChooserBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(finalDateChooserBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(timeGapPrdBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(showDataBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    datePanelLayout.setVerticalGroup(
        datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(datePanelLayout.createSequentialGroup()
            .addComponent(startDateChooserBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(finalDateChooserBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(timeGapPrdBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(showDataBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
    topPanel.setLayout(topPanelLayout);
    topPanelLayout.setHorizontalGroup(
        topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(topPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(datePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(inputSumFld, javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(balanceLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(topPanelLayout.createSequentialGroup()
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(oprtComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(delBaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
                .addComponent(balanceFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );
    topPanelLayout.setVerticalGroup(
        topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(topPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(topPanelLayout.createSequentialGroup()
                    .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(balanceLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(balanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputSumFld, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(oprtComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(delBaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(datePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jLabel1.setText("Доход");

    jLabel3.setText("Расход");

    opertsBtn.setBackground(new java.awt.Color(255, 255, 255));
    opertsBtn.setText("Операции");
    opertsBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            opertsBtnActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(expValLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(opertsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(incValLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE))))
                .addComponent(tableScrollPane))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(incValLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(expValLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(opertsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void opertsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opertsBtnActionPerformed
        new OperationsFrame(this, launcher, true, typeComboBox.getSelectedIndex()).setVisible(true);
    }//GEN-LAST:event_opertsBtnActionPerformed

    private void typeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeComboBoxActionPerformed
        updateComboBox(typeComboBox.getSelectedIndex());
    }//GEN-LAST:event_typeComboBoxActionPerformed

    private void inputSumFldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputSumFldKeyReleased
        if( !inputSumFld.getText().equals("") ){
            addBtn.setEnabled(true);
            if(inputSumFld.getText().length() > 10){
                String text = inputSumFld.getText().substring(0, 10);
                inputSumFld.setText(text);
            }
        }
        else addBtn.setEnabled(false);
    }//GEN-LAST:event_inputSumFldKeyReleased

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        try {
            String name = (String)oprtComboBox.getSelectedItem();
            double value = Double.parseDouble( inputSumFld.getText() );
            int type = typeComboBox.getSelectedIndex()==0 ? 1: -1;
            launcher.TRSCTS.add(name, value, type);
            inputSumFld.setText("");
            updateData();
        } catch (SQLException ex) {
            Logger.getLogger(TrsctFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addBtnActionPerformed

    private void delBaseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delBaseBtnActionPerformed
        launcher.TRSCTS.clearTable();
        try {updateData();} 
        catch (SQLException ex){Logger.getLogger(TrsctFrame.class.getName()).log(Level.SEVERE, null, ex);}
    }//GEN-LAST:event_delBaseBtnActionPerformed

    private void inputSumFldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inputSumFldMouseClicked
        inputSumFld.setText("");
    }//GEN-LAST:event_inputSumFldMouseClicked

    private void timeGapPrdBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeGapPrdBoxActionPerformed
        try {updateData();
        } catch (SQLException ex) {Logger.getLogger(TrsctFrame.class.getName()).log(Level.SEVERE, null, ex);}
    }//GEN-LAST:event_timeGapPrdBoxActionPerformed

    private void showDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showDataBtnActionPerformed
        startDateCldr = (GregorianCalendar) startDateChooserBox.getSelectedDate();
        startDateCldr = homebudget.HomeBudget.setHourZero(startDateCldr);
        finalDateCldr = (GregorianCalendar) finalDateChooserBox.getSelectedDate();
        try {
            isManualData = true;
            updateData();
        } catch (SQLException ex) {
            Logger.getLogger(TrsctFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_showDataBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JLabel balanceFld;
    private javax.swing.JLabel balanceLbl;
    private javax.swing.JPanel datePanel;
    private javax.swing.JButton delBaseBtn;
    private javax.swing.JLabel expValLbl;
    private datechooser.beans.DateChooserCombo finalDateChooserBox;
    private javax.swing.JLabel incValLbl;
    private javax.swing.JTextField inputSumFld;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton opertsBtn;
    private javax.swing.JComboBox<String> oprtComboBox;
    private javax.swing.JButton showDataBtn;
    private datechooser.beans.DateChooserCombo startDateChooserBox;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JComboBox<String> timeGapPrdBox;
    private javax.swing.JPanel topPanel;
    private javax.swing.JComboBox<String> typeComboBox;
    // End of variables declaration//GEN-END:variables
}
