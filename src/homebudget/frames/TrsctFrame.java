package homebudget.frames;

import com.sun.glass.ui.Robot;
import homebudget.views.TsctTableCellRender;
import homebudget.models.OperationsTableLine;
import homebudget.models.TransactionsTableModel;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
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
        Font digitalFont = launcher.DIGITAL_FONT.deriveFont( Font.PLAIN, 30 );
        // баланс
        balanceFld.setFont(digitalFont);
        digitalFont = digitalFont.deriveFont(Font.PLAIN, 20);
        balanceLbl.setFont(digitalFont);
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
        // выбор ручного ввода даты
        if(choicePar == 3){
            startDateChooserBox.setMinDate(FIRST_DATE_RECORD);
            finalDateChooserBox.setMinDate(startDateChooserBox.getSelectedDate());
            startDateChooserBox.setMaxDate(LAST_DATE_RECORD);
            showDataBtn.setEnabled(true);
            lockDateChoice(false);
            return;
        }
        // обнуление стартовой датыдля других choicePar
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
        if( choicePar == 2 )
            table.setModel( new TransactionsTableModel(launcher.TRSCTS.getData()));
        else
            table.setModel( new TransactionsTableModel(launcher.TRSCTS.getData(startDate.getTime(), LAST_DATE_RECORD.getTime())));
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
        jPanel1 = new javax.swing.JPanel();
        balanceLbl = new javax.swing.JLabel();
        balanceFld = new javax.swing.JLabel();
        timeGapPrdBox = new javax.swing.JComboBox<>();
        showDataBtn = new javax.swing.JButton();
        inputSumFld = new javax.swing.JTextField();
        typeComboBox = new javax.swing.JComboBox<>();
        oprtComboBox = new javax.swing.JComboBox<>();
        addBtn = new javax.swing.JButton();
        delBaseBtn = new javax.swing.JButton();
        opertsBtn = new javax.swing.JButton();
        startDateChooserBox = new datechooser.beans.DateChooserCombo();
        finalDateChooserBox = new datechooser.beans.DateChooserCombo();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Домашний бюджет 2.104");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        tableScrollPane.setBackground(new java.awt.Color(255, 255, 255));

        tableScrollPane.setViewportView(table);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        balanceLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        balanceLbl.setText("BALANCE");
        balanceLbl.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        balanceFld.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        balanceFld.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        timeGapPrdBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Последний день", "Последний месяц", "Все время", "Вручную" }));
        timeGapPrdBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeGapPrdBoxActionPerformed(evt);
            }
        });

        showDataBtn.setBackground(new java.awt.Color(255, 255, 255));
        showDataBtn.setText("Показать");
        showDataBtn.setEnabled(false);

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

        opertsBtn.setBackground(new java.awt.Color(255, 255, 255));
        opertsBtn.setText("Операции");
        opertsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opertsBtnActionPerformed(evt);
            }
        });

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
    startDateChooserBox.addSelectionChangedListener(new datechooser.events.SelectionChangedListener() {
        public void onSelectionChange(datechooser.events.SelectionChangedEvent evt) {
            startDateChooserBoxOnSelectionChange(evt);
        }
    });
    startDateChooserBox.addCommitListener(new datechooser.events.CommitListener() {
        public void onCommit(datechooser.events.CommitEvent evt) {
            startDateChooserBoxOnCommit(evt);
        }
    });

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
finalDateChooserBox.addSelectionChangedListener(new datechooser.events.SelectionChangedListener() {
    public void onSelectionChange(datechooser.events.SelectionChangedEvent evt) {
        finalDateChooserBoxOnSelectionChange(evt);
    }
    });
    finalDateChooserBox.addCommitListener(new datechooser.events.CommitListener() {
        public void onCommit(datechooser.events.CommitEvent evt) {
            finalDateChooserBoxOnCommit(evt);
        }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(startDateChooserBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(opertsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addComponent(inputSumFld, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(oprtComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(delBaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(71, 71, 71))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(balanceLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(timeGapPrdBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(balanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(showDataBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(finalDateChooserBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(opertsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(startDateChooserBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(finalDateChooserBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(timeGapPrdBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
            .addComponent(showDataBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(balanceLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(balanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(54, 54, 54)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputSumFld, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(oprtComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(delBaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tableScrollPane))
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGap(14, 14, 14)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
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

    private void startDateChooserBoxOnCommit(datechooser.events.CommitEvent evt) {//GEN-FIRST:event_startDateChooserBoxOnCommit

    }//GEN-LAST:event_startDateChooserBoxOnCommit

    private void finalDateChooserBoxOnCommit(datechooser.events.CommitEvent evt) {//GEN-FIRST:event_finalDateChooserBoxOnCommit

    }//GEN-LAST:event_finalDateChooserBoxOnCommit

    private void finalDateChooserBoxOnSelectionChange(datechooser.events.SelectionChangedEvent evt) {//GEN-FIRST:event_finalDateChooserBoxOnSelectionChange

    }//GEN-LAST:event_finalDateChooserBoxOnSelectionChange

    private void startDateChooserBoxOnSelectionChange(datechooser.events.SelectionChangedEvent evt) {//GEN-FIRST:event_startDateChooserBoxOnSelectionChange

    }//GEN-LAST:event_startDateChooserBoxOnSelectionChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JLabel balanceFld;
    private javax.swing.JLabel balanceLbl;
    private javax.swing.JButton delBaseBtn;
    private datechooser.beans.DateChooserCombo finalDateChooserBox;
    private javax.swing.JTextField inputSumFld;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton opertsBtn;
    private javax.swing.JComboBox<String> oprtComboBox;
    private javax.swing.JButton showDataBtn;
    private datechooser.beans.DateChooserCombo startDateChooserBox;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JComboBox<String> timeGapPrdBox;
    private javax.swing.JComboBox<String> typeComboBox;
    // End of variables declaration//GEN-END:variables
}
