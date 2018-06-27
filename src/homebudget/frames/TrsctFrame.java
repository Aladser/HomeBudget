package homebudget.frames;

import homebudget.HomeBudget;
import homebudget.controllers.TranscationsTableCtrl;
import homebudget.views.TsctTableCellRender;
import homebudget.models.OperationsTableLine;
import homebudget.models.TransactionsTableModel;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.table.JTableHeader;

public class TrsctFrame extends javax.swing.JFrame {
    final homebudget.HomeBudget launcher;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    final long DAY_IN_MS = 86400000;
    final TsctTableCellRender tableRender = new TsctTableCellRender();
    /** Дата первой записи */
    final GregorianCalendar FIRST_DATE_RECORD; 
    /** есть вручную введенные даты */
    boolean isManualData = false;
    /** кэш ввода суммы */
    String chashNumberInput = "";
    /** размеры  окна */
    final int F_WIDTH = 984;
    final int F_HEIGHT = 715;
    final int xCenter;
    //final int yCenter;

    public TrsctFrame(HomeBudget launcher) throws SQLException, AWTException {
        this.launcher = launcher;
        // инициализация крайних дат
        FIRST_DATE_RECORD = launcher.TRSCTS.getFirstRecordDate();
        // рендеринг окна
        initComponents();
        getContentPane().setBackground(Color.white);
        setIconImage( new ImageIcon( getClass().getResource("images/logo.png") ).getImage() );
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        xCenter = (screenSize.width - getWidth())/2;
        setBounds((screenSize.width - getWidth())/2, 0, getWidth(), screenSize.height-50);
        table.requestFocus();
        startDateChooserBox.setFormat(1);
        finalDateChooserBox.setFormat(1);
        tableScrollPane.setOpaque(true);
        tableScrollPane.getViewport().setBackground(Color.white);
        updateComboBox(typeComboBox.getSelectedIndex());
        // шрифты
        balanceLbl.setFont(HomeBudget.DIGFONT.deriveFont(Font.PLAIN, 30));
        balanceFld.setFont(HomeBudget.DIGFONT.deriveFont(Font.PLAIN, 50));
        Font digitalFont = HomeBudget.DIGFONT.deriveFont( Font.PLAIN, 25 );
        incValLbl.setFont(digitalFont);
        expValLbl.setFont(digitalFont);
        incTextLdl.setFont(digitalFont);
        expTextLdl.setFont(digitalFont);
        Font digitalFont2 = HomeBudget.DIGFONT.deriveFont( Font.PLAIN, 20 );
        incStatFld.setFont(digitalFont2);
        expStatFld.setFont(digitalFont2);
        rsrvLbl.setFont(HomeBudget.DIGFONT.deriveFont(Font.PLAIN, 15));
        rsrvFld.setFont(HomeBudget.DIGFONT.deriveFont(Font.PLAIN, 25));
        // background элементов
        oprtComboBox.setBackground(Color.white);
        typeComboBox.setBackground(Color.white);
        timeGapPrdBox.setBackground(Color.white);
        // иконки кнопок
        addBtn.setIcon( new ImageIcon(getClass().getResource("images/addIcon.png")) );
        delBaseBtn.setIcon( new ImageIcon(getClass().getResource("images/returnIcon.png")) );
        editBtn.setIcon( new ImageIcon(getClass().getResource("images/editIcon.png")) );
        // рендер таблицы
        table.setRowHeight(40);
        JTableHeader tableHeader = table.getTableHeader();  
        tableHeader.setFont(new Font("Times New Roman", 1, 14));
        tableHeader.setBackground(new Color(240,240,240));
        // первая настройка данных окна
        startDateChooserBox.setMaxDate(new GregorianCalendar());
        GregorianCalendar startCldr = new GregorianCalendar();
        startCldr.setTimeInMillis(startCldr.getTimeInMillis()-7*86400000);
        startDateChooserBox.setSelectedDate(startCldr);
        finalDateChooserBox.setMaxDate(new GregorianCalendar());
        GregorianCalendar finalCldr = new GregorianCalendar();
        rsrvFld.setText(HomeBudget.formatMoney(launcher.getReserveValue())+" Р");
        updateData(startCldr, finalCldr);
    }
   
    // модель choiceOprtComboBox
    public void updateComboBox(int type){
        ArrayList<OperationsTableLine> data = launcher.OPRTS.getData(typeComboBox.getSelectedIndex());
        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>();
        for(int i=0; i<data.size(); i++) cbModel.addElement(data.get(i).name);
        oprtComboBox.setModel(cbModel);        
    }
    
    // обновляет данные таблицы и полей
    private void updateData(GregorianCalendar startDate, GregorianCalendar finalDate){
        startDate = HomeBudget.setHourZero(startDate);
        finalDate = HomeBudget.setFinalDate(finalDate);
        // таблица
        table.setModel( new TransactionsTableModel(launcher.TRSCTS.getData(startDate, finalDate)));
        table.getColumnModel().getColumn(0).setCellRenderer(tableRender);
        table.getColumnModel().getColumn(1).setCellRenderer(tableRender);
        table.getColumnModel().getColumn(2).setCellRenderer(tableRender); 
        // доход, расход
        ArrayList<TranscationsTableCtrl.OperationStatistic> data;
        data = launcher.TRSCTS.getTrsctStatistic(0, startDate, finalDate);
        String strData = "\n";
        for(int i=0; i<data.size(); i++)
            strData += "  " + data.get(i).name + ": " + HomeBudget.formatMoney(data.get(i).value) + "р. \n  (" + data.get(i).percent + "%)\n\n";
        incStatFld.setText(strData);
        data = launcher.TRSCTS.getTrsctStatistic(1, startDate, finalDate);
        strData = "\n";
        for(int i=0; i<data.size(); i++)
            strData += "  " + data.get(i).name + ": " + HomeBudget.formatMoney(data.get(i).value) + "р. \n  (" + data.get(i).percent + "%)\n\n";
        expStatFld.setText(strData);
        incValLbl.setText(HomeBudget.formatMoney(launcher.TRSCTS.getTotalBudgetPart(true, startDate, finalDate))+" P");
        expValLbl.setText(HomeBudget.formatMoney(launcher.TRSCTS.getTotalBudgetPart(false, startDate, finalDate))+" P");
        // баланс
        try {
            double balance = launcher.TRSCTS.getBalance() - launcher.getReserveValue();
            balanceFld.setText(HomeBudget.formatMoney(balance)+" P");
        } catch (SQLException ex) {
            Logger.getLogger(TrsctFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        opertsBtn = new javax.swing.JButton();
        rsrvLbl = new javax.swing.JLabel();
        rsrvFld = new javax.swing.JLabel();
        editBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        incStatFld = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        expStatFld = new javax.swing.JTextPane();
        incValLbl = new javax.swing.JLabel();
        incTextLdl = new javax.swing.JLabel();
        expValLbl = new javax.swing.JLabel();
        expTextLdl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Домашний бюджет 2.1");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });

        tableScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        tableScrollPane.setBorder(null);
        tableScrollPane.setForeground(new java.awt.Color(255, 255, 255));

        tableScrollPane.setViewportView(table);

        topPanel.setBackground(new java.awt.Color(255, 255, 255));
        topPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        balanceLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        balanceLbl.setText("Баланс");

        balanceFld.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        inputSumFld.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
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
        datePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Период", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
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
    startDateChooserBox.setNothingAllowed(false);
    startDateChooserBox.setCalendarBackground(new java.awt.Color(255, 255, 255));
    startDateChooserBox.setFieldFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
    startDateChooserBox.setNavigateFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
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
finalDateChooserBox.setCalendarBackground(new java.awt.Color(255, 255, 255));
finalDateChooserBox.setFieldFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
finalDateChooserBox.setNavigateFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));

timeGapPrdBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Последний день", "Последняя неделя", "Последний месяц", "Все время", "Вручную" }));
timeGapPrdBox.setSelectedIndex(1);
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

    jLabel1.setFont(new java.awt.Font("Viner Hand ITC", 1, 13)); // NOI18N
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText(" -");

    javax.swing.GroupLayout datePanelLayout = new javax.swing.GroupLayout(datePanel);
    datePanel.setLayout(datePanelLayout);
    datePanelLayout.setHorizontalGroup(
        datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, datePanelLayout.createSequentialGroup()
            .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(datePanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(timeGapPrdBox, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(datePanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(startDateChooserBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, datePanelLayout.createSequentialGroup()
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(finalDateChooserBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(showDataBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );
    datePanelLayout.setVerticalGroup(
        datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(datePanelLayout.createSequentialGroup()
            .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(startDateChooserBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                .addComponent(finalDateChooserBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(timeGapPrdBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(showDataBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    opertsBtn.setBackground(new java.awt.Color(255, 255, 255));
    opertsBtn.setText("Операции");
    opertsBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            opertsBtnActionPerformed(evt);
        }
    });

    rsrvLbl.setBackground(new Color(200,200,200));
    rsrvLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    rsrvLbl.setText("Резерв");
    rsrvLbl.setToolTipText("");

    rsrvFld.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

    editBtn.setBackground(new java.awt.Color(255, 255, 255));
    editBtn.setToolTipText("Изменить резерв");

    javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
    topPanel.setLayout(topPanelLayout);
    topPanelLayout.setHorizontalGroup(
        topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(topPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(datePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(inputSumFld, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(topPanelLayout.createSequentialGroup()
                    .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(delBaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(topPanelLayout.createSequentialGroup()
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(oprtComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(balanceFld, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(balanceLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rsrvLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(rsrvFld, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                    .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(opertsBtn)))
            .addContainerGap())
    );
    topPanelLayout.setVerticalGroup(
        topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(topPanelLayout.createSequentialGroup()
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(topPanelLayout.createSequentialGroup()
                    .addComponent(datePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(inputSumFld, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(oprtComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(delBaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(topPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(balanceLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(balanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rsrvLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rsrvFld, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(opertsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    incStatFld.setForeground(new java.awt.Color(0, 153, 0));
    incStatFld.setMaximumSize(new java.awt.Dimension(2147483647, 10000));
    incStatFld.setMinimumSize(new java.awt.Dimension(6, 500));
    incStatFld.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            incStatFldMouseClicked(evt);
        }
    });
    jScrollPane1.setViewportView(incStatFld);

    expStatFld.setForeground(new java.awt.Color(255, 0, 0));
    expStatFld.setMaximumSize(new java.awt.Dimension(2147483647, 400));
    expStatFld.setMinimumSize(new java.awt.Dimension(6, 400));
    expStatFld.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            expStatFldMouseClicked(evt);
        }
    });
    jScrollPane2.setViewportView(expStatFld);

    incValLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

    incTextLdl.setText("Доход");

    expValLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

    expTextLdl.setText("Расход");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tableScrollPane))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(incTextLdl, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(incValLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(11, 11, 11))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(expTextLdl, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(expValLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(14, 14, 14))
                        .addComponent(jScrollPane1))
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(23, Short.MAX_VALUE))))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(incTextLdl, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(incValLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(expTextLdl, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(expValLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(13, 13, 13)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)))
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
            try{
                Double.parseDouble(inputSumFld.getText());
                chashNumberInput = inputSumFld.getText();
            }catch(java.lang.NumberFormatException e){
                inputSumFld.setText(chashNumberInput);
            }
        }
        else addBtn.setEnabled(false);
    }//GEN-LAST:event_inputSumFldKeyReleased

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        String name = (String)oprtComboBox.getSelectedItem();
        double value = Double.parseDouble( inputSumFld.getText() );
        int type = typeComboBox.getSelectedIndex()==0 ? 1: -1;
        launcher.TRSCTS.add(name, value, type);
        inputSumFld.setText("");
        GregorianCalendar startDate = (GregorianCalendar) startDateChooserBox.getSelectedDate();
        GregorianCalendar finalDate = (GregorianCalendar) finalDateChooserBox.getSelectedDate();
        updateData(startDate, finalDate);
    }//GEN-LAST:event_addBtnActionPerformed

    private void delBaseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delBaseBtnActionPerformed
        launcher.TRSCTS.removeLast();
        GregorianCalendar startDate = (GregorianCalendar) startDateChooserBox.getSelectedDate();
        GregorianCalendar finalDate = (GregorianCalendar) finalDateChooserBox.getSelectedDate();
        updateData(startDate, finalDate);
    }//GEN-LAST:event_delBaseBtnActionPerformed

    private void inputSumFldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inputSumFldMouseClicked
        inputSumFld.setText("");
    }//GEN-LAST:event_inputSumFldMouseClicked

    private void timeGapPrdBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeGapPrdBoxActionPerformed
        GregorianCalendar startDate;
        int choicePar = timeGapPrdBox.getSelectedIndex();
        startDateChooserBox.setMinDate(FIRST_DATE_RECORD);
        if(choicePar != 3){
            showDataBtn.setEnabled(false);
            startDateChooserBox.setLocked(true);
            finalDateChooserBox.setLocked(true);
        }
        switch(choicePar){
            case 0:
                startDate = HomeBudget.setHourZero(new GregorianCalendar());
                break;
            case 1:
                startDate = HomeBudget.setHourZero(new GregorianCalendar());
                startDate.setTimeInMillis(startDate.getTimeInMillis()-86400000*7);
                break;
            case 2:
                startDate = HomeBudget.setHourZero(new GregorianCalendar());
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case 3:
                startDate = FIRST_DATE_RECORD;
                break;
            default:
                showDataBtn.setEnabled(true);
                startDateChooserBox.setLocked(false);
                finalDateChooserBox.setLocked(false);
                return;
        }
        startDateChooserBox.setLocked(false);
        finalDateChooserBox.setLocked(false);
        long lLimit = startDate.getTimeInMillis() - 1;
        GregorianCalendar limit = new GregorianCalendar();
        limit.setTimeInMillis(lLimit);
        startDateChooserBox.setMinDate(limit);
        startDateChooserBox.setSelectedDate(startDate);
        limit = new GregorianCalendar();
        lLimit = new GregorianCalendar().getTimeInMillis()+1;
        limit.setTimeInMillis(lLimit);
        finalDateChooserBox.setMaxDate(limit);
        finalDateChooserBox.setSelectedDate(new GregorianCalendar());
        startDateChooserBox.setLocked(true);
        finalDateChooserBox.setLocked(true);
        updateData(startDate, new GregorianCalendar());
    }//GEN-LAST:event_timeGapPrdBoxActionPerformed

    private void showDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showDataBtnActionPerformed
        GregorianCalendar startDate = (GregorianCalendar) startDateChooserBox.getSelectedDate();
        GregorianCalendar finalDate = (GregorianCalendar) finalDateChooserBox.getSelectedDate();
        isManualData = true;
        updateData(startDate, finalDate);
    }//GEN-LAST:event_showDataBtnActionPerformed

    private void startDateChooserBoxOnCommit(datechooser.events.CommitEvent evt) {//GEN-FIRST:event_startDateChooserBoxOnCommit
        GregorianCalendar startDate = (GregorianCalendar) startDateChooserBox.getSelectedDate();
        GregorianCalendar finalDate = (GregorianCalendar) finalDateChooserBox.getSelectedDate();
        
        if(startDate.getTimeInMillis() > finalDate.getTimeInMillis()){
            finalDate.setTimeInMillis(startDate.getTimeInMillis());
            finalDateChooserBox.setSelectedDate(startDate);
        }
    }//GEN-LAST:event_startDateChooserBoxOnCommit

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if((evt.getNewState() & WindowEvent.WINDOW_ICONIFIED) !=0)
            setBounds(xCenter, 0, F_WIDTH , (int) screenSize.getHeight());
    }//GEN-LAST:event_formWindowStateChanged

    private void incStatFldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_incStatFldMouseClicked
        requestFocus();
    }//GEN-LAST:event_incStatFldMouseClicked

    private void expStatFldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expStatFldMouseClicked
        requestFocus();
    }//GEN-LAST:event_expStatFldMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JLabel balanceFld;
    private javax.swing.JLabel balanceLbl;
    private javax.swing.JPanel datePanel;
    private javax.swing.JButton delBaseBtn;
    private javax.swing.JButton editBtn;
    private javax.swing.JTextPane expStatFld;
    private javax.swing.JLabel expTextLdl;
    private javax.swing.JLabel expValLbl;
    private datechooser.beans.DateChooserCombo finalDateChooserBox;
    private javax.swing.JTextPane incStatFld;
    private javax.swing.JLabel incTextLdl;
    private javax.swing.JLabel incValLbl;
    private javax.swing.JTextField inputSumFld;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton opertsBtn;
    private javax.swing.JComboBox<String> oprtComboBox;
    private javax.swing.JLabel rsrvFld;
    private javax.swing.JLabel rsrvLbl;
    private javax.swing.JButton showDataBtn;
    private datechooser.beans.DateChooserCombo startDateChooserBox;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JComboBox<String> timeGapPrdBox;
    private javax.swing.JPanel topPanel;
    private javax.swing.JComboBox<String> typeComboBox;
    // End of variables declaration//GEN-END:variables
}
