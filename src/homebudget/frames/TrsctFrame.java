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
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.JTableHeader;

public class TrsctFrame extends javax.swing.JFrame {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    final long DAY_IN_MS = 86400000;
    final TsctTableCellRender tableRender = new TsctTableCellRender();
    /** Дата первой записи */
    final GregorianCalendar FIRST_DATE_RECORD; 
    /** есть вручную введенные даты */
    boolean isManualData = false;
    /** размеры  окна */
    final int F_WIDTH = 984;
    final int F_HEIGHT = 715;
    final int xCenter;
    /** цифровой шрифт */
    public Font DIGFONT;

    public TrsctFrame() throws SQLException, AWTException {
        // цифровой шрифт
        try {
            DIGFONT = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("resources/digFont.ttf"));
        } catch (IOException | FontFormatException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Не удалось установить шрифт digital.ttf!. Будет использован стандартный шрифт."
            );
            DIGFONT = new Font("Consolas", Font.PLAIN, 15);
        }
        // инициализация крайних дат
        FIRST_DATE_RECORD = HomeBudget.TRSCTS.getFirstRecordDate();
        // рендеринг окна
        initComponents();
        getContentPane().setBackground(Color.white);
        setIconImage( new ImageIcon( getClass().getResource("resources/images/logo.png") ).getImage() );
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
        balanceLbl.setFont(DIGFONT.deriveFont(Font.PLAIN, 30));
        balanceFld.setFont(DIGFONT.deriveFont(Font.PLAIN, 50));
        Font digitalFont = DIGFONT.deriveFont( Font.PLAIN, 25 );
        incValLbl.setFont(digitalFont);
        expValLbl.setFont(digitalFont);
        incTextLdl.setFont(digitalFont);
        expTextLdl.setFont(digitalFont);
        digitalFont = DIGFONT.deriveFont( Font.PLAIN, 20 );
        incStatFld.setFont(digitalFont);
        expStatFld.setFont(digitalFont);
        rsrvLbl.setFont(DIGFONT.deriveFont(Font.PLAIN, 15));
        rsrvFld.setFont(DIGFONT.deriveFont(Font.PLAIN, 25));
        inputSumFld.setFont(DIGFONT.deriveFont(Font.PLAIN, 20));
        // background элементов
        oprtComboBox.setBackground(Color.white);
        typeComboBox.setBackground(Color.white);
        timeGapPrdBox.setBackground(Color.white);
        // иконки кнопок
        addBtn.setIcon( new ImageIcon(getClass().getResource("resources/images/addIcon.png")) );
        backOptrBtn.setIcon( new ImageIcon(getClass().getResource("resources/images/returnIcon.png")) );
        editBtn.setIcon( new ImageIcon(getClass().getResource("resources/images/editIcon.png")) );
        opertsBtn.setIcon( new ImageIcon(getClass().getResource("resources/images/oprtsIcon.png")) );
        delBaseBtn.setIcon( new ImageIcon(getClass().getResource("resources/images/delIcon.png")) );
        infoBtn.setIcon( new ImageIcon(getClass().getResource("resources/images/infoIcon.png")) );
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
        updateData(startCldr, finalCldr);
        // резервная сумма
        rsrvFld.setText(HomeBudget.formatMoney(HomeBudget.getReserveValue())+" Р");
    }
   
    // модель choiceOprtComboBox
    public void updateComboBox(int type){
        ArrayList<OperationsTableLine> data = HomeBudget.OPRTS.getData(typeComboBox.getSelectedIndex());
        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>();
        for(int i=0; i<data.size(); i++) cbModel.addElement(data.get(i).name);
        oprtComboBox.setModel(cbModel);        
    }
    
    /** обновить баланс и резерв */
    public void updateReserveFld(){
        rsrvFld.setText(HomeBudget.formatMoney(HomeBudget.getReserveValue())+" P");
        double balance;
        try {
            balance = HomeBudget.TRSCTS.getBalance() - HomeBudget.getReserveValue();
            balanceFld.setText(HomeBudget.formatMoney(balance)+" P");
        } catch (SQLException ex) {
            System.err.println("Ошибка TrsctFrame.updateReserve()");
            Logger.getLogger(TrsctFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** обновляет данные таблицы и полей */
    private void updateData(GregorianCalendar startDate, GregorianCalendar finalDate){
        startDate = HomeBudget.setDayStart(startDate);
        finalDate = HomeBudget.setDayFinal(finalDate);
        // таблица
        table.setModel( new TransactionsTableModel(HomeBudget.TRSCTS.getData(startDate, finalDate)));
        table.getColumnModel().getColumn(0).setCellRenderer(tableRender);
        table.getColumnModel().getColumn(1).setCellRenderer(tableRender);
        table.getColumnModel().getColumn(2).setCellRenderer(tableRender); 
        // доход, расход
        ArrayList<TranscationsTableCtrl.OperationStatistic> data;
        data = HomeBudget.TRSCTS.getTrsctStatistic(0, startDate, finalDate);
        String strData = "\n";
        for(int i=0; i<data.size(); i++)
            strData += "  " + data.get(i).name + ": " + HomeBudget.formatMoney(data.get(i).value) + "р. \n  (" + data.get(i).percent + "%)\n";
        incStatFld.setText(strData);
        data = HomeBudget.TRSCTS.getTrsctStatistic(1, startDate, finalDate);
        strData = "\n";
        for(int i=0; i<data.size(); i++)
            strData += "  " + data.get(i).name + ": " + HomeBudget.formatMoney(data.get(i).value) + "р. \n  (" + data.get(i).percent + "%)\n";
        expStatFld.setText(strData);
        incValLbl.setText(HomeBudget.formatMoney(HomeBudget.TRSCTS.getTotalBudgetPart(true, startDate, finalDate))+" P");
        expValLbl.setText(HomeBudget.formatMoney(HomeBudget.TRSCTS.getTotalBudgetPart(false, startDate, finalDate))+" P");
        // баланс
        try {
            double balance = HomeBudget.TRSCTS.getBalance() - HomeBudget.getReserveValue();
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
        addBtn = new javax.swing.JButton();
        backOptrBtn = new javax.swing.JButton();
        datePanel = new javax.swing.JPanel();
        startDateChooserBox = new datechooser.beans.DateChooserCombo();
        finalDateChooserBox = new datechooser.beans.DateChooserCombo();
        timeGapPrdBox = new javax.swing.JComboBox<String>();
        showDataBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        opertsBtn = new javax.swing.JButton();
        rsrvLbl = new javax.swing.JLabel();
        rsrvFld = new javax.swing.JLabel();
        editBtn = new javax.swing.JButton();
        typeComboBox = new javax.swing.JComboBox<String>();
        oprtComboBox = new javax.swing.JComboBox<String>();
        delBaseBtn = new javax.swing.JButton();
        infoBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        incStatFld = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        expStatFld = new javax.swing.JTextPane();
        incValLbl = new javax.swing.JLabel();
        incTextLdl = new javax.swing.JLabel();
        expValLbl = new javax.swing.JLabel();
        expTextLdl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Домашний бюджет v1.2");
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

        balanceLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        balanceLbl.setText("Баланс");

        balanceFld.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

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

        addBtn.setBackground(new java.awt.Color(255, 255, 255));
        addBtn.setToolTipText("Добавить");
        addBtn.setEnabled(false);
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        backOptrBtn.setBackground(new java.awt.Color(255, 255, 255));
        backOptrBtn.setToolTipText("Отменить последнюю операцию");
        backOptrBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backOptrBtnActionPerformed(evt);
            }
        });

        datePanel.setBackground(new java.awt.Color(255, 255, 255));
        datePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Период", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        datePanel.setToolTipText("");

        startDateChooserBox.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
        startDateChooserBox.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
            new datechooser.view.appearance.ViewAppearance("custom",
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    true,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(0, 0, 255),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(128, 128, 128),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.LabelPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.LabelPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
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
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                new java.awt.Color(0, 0, 0),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                new java.awt.Color(0, 0, 0),
                new java.awt.Color(0, 0, 255),
                true,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                new java.awt.Color(0, 0, 255),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.ButtonPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                new java.awt.Color(128, 128, 128),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.LabelPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                new java.awt.Color(0, 0, 0),
                new java.awt.Color(0, 0, 255),
                false,
                true,
                new datechooser.view.appearance.swing.LabelPainter()),
            new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
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
finalDateChooserBox.addSelectionChangedListener(new datechooser.events.SelectionChangedListener() {
    public void onSelectionChange(datechooser.events.SelectionChangedEvent evt) {
        finalDateChooserBoxOnSelectionChange(evt);
    }
    });

    timeGapPrdBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Последний день", "Последняя неделя", "Последний месяц", "Все время", "Вручную" }));
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
            .addContainerGap()
            .addComponent(startDateChooserBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(finalDateChooserBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(timeGapPrdBox, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(showDataBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    datePanelLayout.setVerticalGroup(
        datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(datePanelLayout.createSequentialGroup()
            .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(startDateChooserBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(finalDateChooserBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeGapPrdBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showDataBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    opertsBtn.setBackground(new java.awt.Color(255, 255, 255));
    opertsBtn.setToolTipText("Операции");
    opertsBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            opertsBtnActionPerformed(evt);
        }
    });

    rsrvLbl.setBackground(new Color(200,200,200));
    rsrvLbl.setForeground(new java.awt.Color(51, 204, 255));
    rsrvLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    rsrvLbl.setText("Резерв");
    rsrvLbl.setToolTipText("");

    rsrvFld.setForeground(new java.awt.Color(51, 204, 255));
    rsrvFld.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

    editBtn.setBackground(new java.awt.Color(255, 255, 255));
    editBtn.setToolTipText("Изменить резерв");
    editBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            editBtnActionPerformed(evt);
        }
    });

    typeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Доход", "Расход" }));
    typeComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            typeComboBoxActionPerformed(evt);
        }
    });

    delBaseBtn.setBackground(new java.awt.Color(255, 255, 255));
    delBaseBtn.setToolTipText("Удалить базу");
    delBaseBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            delBaseBtnActionPerformed(evt);
        }
    });

    infoBtn.setBackground(new java.awt.Color(255, 255, 255));
    infoBtn.setToolTipText("Удалить базу");
    infoBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            infoBtnActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
    topPanel.setLayout(topPanelLayout);
    topPanelLayout.setHorizontalGroup(
        topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(topPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                    .addComponent(balanceLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(262, 262, 262))
                .addGroup(topPanelLayout.createSequentialGroup()
                    .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(datePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, topPanelLayout.createSequentialGroup()
                            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(inputSumFld, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(typeComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 170, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(topPanelLayout.createSequentialGroup()
                                    .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(backOptrBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(212, 212, 212))
                                .addGroup(topPanelLayout.createSequentialGroup()
                                    .addComponent(oprtComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(infoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(delBaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(opertsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, topPanelLayout.createSequentialGroup()
                            .addComponent(balanceFld, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(topPanelLayout.createSequentialGroup()
                                    .addComponent(rsrvLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(rsrvFld, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
    );
    topPanelLayout.setVerticalGroup(
        topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(topPanelLayout.createSequentialGroup()
            .addComponent(datePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(balanceLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rsrvLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(balanceFld, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addComponent(rsrvFld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(inputSumFld, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(backOptrBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(oprtComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delBaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(opertsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(infoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tableScrollPane))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(expTextLdl, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                    .addComponent(expValLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addComponent(incTextLdl, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(incValLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(jScrollPane1)
                .addComponent(jScrollPane2))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(incValLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(incTextLdl, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(expTextLdl, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(expValLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jScrollPane2))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)))
            .addContainerGap())
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void opertsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opertsBtnActionPerformed
        new OperationsFrame(this, true, typeComboBox.getSelectedIndex()).setVisible(true);
    }//GEN-LAST:event_opertsBtnActionPerformed

    private void typeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeComboBoxActionPerformed
        updateComboBox(typeComboBox.getSelectedIndex());
    }//GEN-LAST:event_typeComboBoxActionPerformed

    // Проверяет корректность ввода цифр в inputSumFld
    private void inputSumFldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputSumFldKeyReleased
        String val = inputSumFld.getText();
        if(val.equals("")){
            addBtn.setEnabled(false);
            return;
        }
        addBtn.setEnabled(true);
        // ограничение длины числа
        if(val.length() > 10){
            val = val.substring(0, 10);
            inputSumFld.setText(val);
            return;
        }
        // проверка ввода цифр
        try{
            Double.parseDouble(val);
            String lastSymb = val.substring(val.length()-1, val.length());
            if(lastSymb.equals("d") || lastSymb.equals("f")) throw new NumberFormatException();
            }
        catch(NumberFormatException e){
            val = val.substring(0, val.length()-1);
            inputSumFld.setText(val);
            return;
        }
        // проверка дробной части
        if(val.contains(".")){
            if(val.indexOf(".") < val.length()-3){
                val = val.substring(0, val.length()-1);
                inputSumFld.setText(val);
            }
        }
    }//GEN-LAST:event_inputSumFldKeyReleased

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        String name = (String)oprtComboBox.getSelectedItem();
        double value = Double.parseDouble( inputSumFld.getText() );
        int type = typeComboBox.getSelectedIndex()==0 ? 1: -1;
        HomeBudget.TRSCTS.add(name, value, type);
        inputSumFld.setText("");
        GregorianCalendar startDate = (GregorianCalendar) startDateChooserBox.getSelectedDate();
        GregorianCalendar finalDate = (GregorianCalendar) finalDateChooserBox.getSelectedDate();
        updateData(startDate, finalDate);
    }//GEN-LAST:event_addBtnActionPerformed

    private void backOptrBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backOptrBtnActionPerformed
        HomeBudget.TRSCTS.removeLast();
        GregorianCalendar startDate = (GregorianCalendar) startDateChooserBox.getSelectedDate();
        GregorianCalendar finalDate = (GregorianCalendar) finalDateChooserBox.getSelectedDate();
        updateData(startDate, finalDate);
    }//GEN-LAST:event_backOptrBtnActionPerformed

    private void inputSumFldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inputSumFldMouseClicked
        inputSumFld.setText("");
    }//GEN-LAST:event_inputSumFldMouseClicked

    private void timeGapPrdBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeGapPrdBoxActionPerformed
        GregorianCalendar startDate;
        int choicePar = timeGapPrdBox.getSelectedIndex();
        startDateChooserBox.setMinDate(FIRST_DATE_RECORD);
        finalDateChooserBox.setMinDate(FIRST_DATE_RECORD);
        if(choicePar != 3){
            showDataBtn.setEnabled(false);
            startDateChooserBox.setLocked(true);
            finalDateChooserBox.setLocked(true);
        }
        switch(choicePar){
            case 0:
                startDate = HomeBudget.setDayStart(new GregorianCalendar());
                break;
            case 1:
                startDate = HomeBudget.setDayStart(new GregorianCalendar());
                startDate.setTimeInMillis(startDate.getTimeInMillis()-86400000*7);
                break;
            case 2:
                startDate = HomeBudget.setDayStart(new GregorianCalendar());
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

    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
        new RsrvDlg(this, true).setVisible(true);
    }//GEN-LAST:event_editBtnActionPerformed

    private void finalDateChooserBoxOnSelectionChange(datechooser.events.SelectionChangedEvent evt) {//GEN-FIRST:event_finalDateChooserBoxOnSelectionChange
        long startDate = startDateChooserBox.getSelectedDate().getTimeInMillis();
        long finalDate = finalDateChooserBox.getSelectedDate().getTimeInMillis(); 
        if(finalDate < startDate){
            GregorianCalendar cldr = new GregorianCalendar();
            cldr.setTimeInMillis(startDate);
            finalDateChooserBox.setSelectedDate(cldr);
        }
    }//GEN-LAST:event_finalDateChooserBoxOnSelectionChange

    private void delBaseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delBaseBtnActionPerformed
        int select=JOptionPane.showConfirmDialog(null,
                "Удалить все данные?",
                "Удаление базы данных",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if(select==0){
            HomeBudget.TRSCTS.removeData();
            HomeBudget.OPRTS.removeData();
            HomeBudget.setReserveValue(0);
            updateReserveFld();
            GregorianCalendar startDate = (GregorianCalendar) startDateChooserBox.getSelectedDate();
            GregorianCalendar finalDate = (GregorianCalendar) finalDateChooserBox.getSelectedDate();
            updateData(startDate, finalDate);
        }        
    }//GEN-LAST:event_delBaseBtnActionPerformed

    private void infoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoBtnActionPerformed
        new infoDialog(this, true).setVisible(true);
    }//GEN-LAST:event_infoBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton backOptrBtn;
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
    private javax.swing.JButton infoBtn;
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
