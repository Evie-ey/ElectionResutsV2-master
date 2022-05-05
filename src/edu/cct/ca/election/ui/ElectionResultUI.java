package edu.cct.ca.election.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.*;
import java.lang.String;
import java.util.List;
import java.util.stream.Collectors;

public class ElectionResultUI{
    private JPanel rootPanel;
    private JTable showTable;
    private JButton uploadFile;
    private JComboBox ConstituencyName;
    private JComboBox CandidateFirstName;
    private JComboBox CandidateSurname;
    private JPanel barPanel;
    private JLabel UploadLabel;
    JLabel label = new JLabel("No records available");
    JLabel label2 = new JLabel("Select data ro view chart");
    private String[][] allData = new String[10000][10000];
    private String[] tableHeader = new String[14];
    int constituencyNameIndex = -1;
    String constituencyNameSearch = "";
    int candidateSurnameIndex = -1;
    String candidateSurnameSearch = "";
    int candidateFirstNameIndex = -1;
    String candidateFirstNameSearch = "";
    public ElectionResultUI() {

        if(allData.length < 1) {
            label.setSize(label.getPreferredSize());
            showTable.add(label, BorderLayout.CENTER);
            showTable.add(label2, BorderLayout.CENTER);

            showTable.setFillsViewportHeight(true);
        }
        uploadFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String line;
                String[][] tableData = new String[10000][10000];

                Set<String> constituencyName = new HashSet<String>();
                Set<String> candidateSurname = new HashSet<String>();
                Set<String> candidateFirstName = new HashSet<String>();

                if(e.getSource() == uploadFile) {
                    JFileChooser upload  = new JFileChooser();
                    int saveFile = upload.showSaveDialog(null);

                    if(saveFile == JFileChooser.APPROVE_OPTION) {
                        File filePath = new File(upload.getSelectedFile().getAbsolutePath());
                        System.out.println(filePath);
                        if ((filePath.getAbsolutePath()).toLowerCase().endsWith(".csv")) {

                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                                int i = 0;
                                while ((line = reader.readLine()) != null) {

                                    String[] values = line.split(",");
                                    if (i == 0) {
                                        tableHeader = values;
                                        List<String> tableHeaderList = Arrays.asList(tableHeader);
                                        constituencyNameIndex = tableHeaderList.indexOf("Constituency Name");
                                        candidateSurnameIndex = tableHeaderList.indexOf("Candidate surname");
                                        candidateFirstNameIndex = tableHeaderList.indexOf("Candidate First Name");
                                    } else {
                                        if (constituencyNameIndex >= 0 && values[constituencyNameIndex].length() > 0) {
                                            constituencyName.add(values[constituencyNameIndex]);
                                        }
                                        if (candidateSurnameIndex >= 0 && values[candidateSurnameIndex].length() > 0) {
                                            candidateSurname.add(values[candidateSurnameIndex]);
                                        }
                                        if (candidateFirstNameIndex >= 0 && values[candidateFirstNameIndex].length() > 0) {
                                            candidateFirstName.add(values[candidateFirstNameIndex]);
                                        }
                                        tableData[i - 1] = values;
                                    }
                                    i++;
                                }
                            } catch (FileNotFoundException fileNotFoundException) {
                                fileNotFoundException.printStackTrace();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                            allData = tableData;

                            createTable(tableHeader, tableData);
                            createBarChart(11, 0, 4, "Constituency Name", "Total Votes");
                            createConstituencyCombo(constituencyName.stream().toArray(n -> new String[n]));
                            createCandidateSurname(candidateSurname.stream().toArray(n -> new String[n]));
                            createCandidateFirstName(candidateFirstName.stream().toArray(n -> new String[n]));
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "You need to upload a csv file");
                        }

                    }
                }
            }
        });
    }

    public ElectionResultUI(int constituencyNameIndex, int candidateSurnameIndex, int candidateFirstNameIndex, String[][] allData){
        this.candidateSurnameIndex = candidateSurnameIndex;
        this.constituencyNameIndex = constituencyNameIndex;
        this.candidateFirstNameIndex = candidateFirstNameIndex;
        this.allData = allData;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    private void createTable(String[] header, String[][] data) {
        showTable.setShowGrid(true);
        showTable.setShowHorizontalLines(true);
        showTable.setShowVerticalLines(true);

        showTable.setModel(new DefaultTableModel(data, header));


//        Column model associated with this table
        TableColumnModel columns = showTable.getColumnModel();
        columns.getColumn(0).setMinWidth(100);

//        adjust centering
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        columns.getColumn(1).setCellRenderer(centerRenderer);
        columns.getColumn(2).setCellRenderer(centerRenderer);

    }

    ItemListener listener = (itemEvent) -> {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            barPanel.removeAll();
            barPanel.revalidate();
            barPanel.repaint();
            createTable(tableHeader, allData);
//            System.out.println(Arrays.asList(allData));

            if (itemEvent.getSource() == ConstituencyName) {

                String searchTerm  = ConstituencyName.getSelectedItem() + "";
                constituencyNameSearch = searchTerm.equals("CONSTITUENCY NAME")? "" : searchTerm;

                String[][] fData = searchData();
                System.out.println("Arrays.asList(fData)");
                System.out.println(allData[3][10]);
                System.out.println("allData[3][10]");
                createTable(tableHeader, fData);
                createBarChart(11, 4, 2, "Candidate  First Name", "Total Votes");

                constituencyNameSearch = "";
            }
            else if (itemEvent.getSource() == CandidateFirstName) {

                String searchTerm = CandidateFirstName.getSelectedItem() + "";
                candidateFirstNameSearch = searchTerm.equals("CANDIDATE FIRST NAME")? "" : searchTerm;
                String[][] fData = searchData();
                createTable(tableHeader, fData);
                createBarChart(11, 0, 4, "Count Number", "Total Votes");
                candidateFirstNameSearch = "";

            }
            else if (itemEvent.getSource() == CandidateSurname) {
                String searchTerm = CandidateSurname.getSelectedItem() + "";
                candidateSurnameSearch = searchTerm.equals("CANDIDATE SURNAME")? "" : searchTerm;
                String[][] fData = searchData();
                createTable(tableHeader, fData);
                createBarChart(11, 0, 4, "Count Number", "Total Votes");
                candidateSurnameSearch = "";
            }
        }
    };

    public String[][] searchData(){
        List<String[]> result = Arrays.stream(allData)
                .filter(value -> ((constituencyNameIndex > -1 && value[constituencyNameIndex] != null && constituencyNameSearch != "" && value[constituencyNameIndex].equals(constituencyNameSearch)) || constituencyNameSearch.equals("")))
                .filter(value -> ((candidateSurnameIndex > -1 && value[candidateSurnameIndex] != null && candidateSurnameSearch != "" && value[candidateSurnameIndex].equals(candidateSurnameSearch)) || candidateSurnameSearch.equals("")))
                .filter(value -> ((candidateFirstNameIndex > -1 && value[candidateFirstNameIndex] != null && candidateFirstNameSearch != "" && value[candidateFirstNameIndex].equals(candidateFirstNameSearch)) || candidateFirstNameSearch.equals(""))).collect(Collectors.toList());
        return result.toArray(String[][]::new);
    }

    private void createConstituencyCombo(String[] constituencyData) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("CONSTITUENCY NAME");
        model.addAll(Arrays.asList(constituencyData));
        ConstituencyName.setModel(model);
        ElectionResultUI ui = new ElectionResultUI(constituencyNameIndex, candidateSurnameIndex, candidateFirstNameIndex, allData);
        ConstituencyName.addItemListener(listener);
    }

    private void createCandidateSurname(String[] candidateSurname) {
        System.out.println("clicked on candidate surname");
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("CANDIDATE SURNAME");
        model.addAll(Arrays.asList(candidateSurname));
        CandidateSurname.setModel(model);
        CandidateSurname.addItemListener(listener);
    }

    private void createCandidateFirstName(String[] candidateFirstName) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("CANDIDATE FIRST NAME");
        model.addAll(Arrays.asList(candidateFirstName));
        CandidateFirstName.setModel(model);
        CandidateFirstName.addItemListener(listener);
    }

    private CategoryDataset createDataset(int votes, int key2Index, int key3Index) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();


        String[][] fData = searchData();

        for(int i = 0; i < fData.length; i++) {
            double totalVotes = 0;

            if(fData[i][votes] == "" || fData[i][votes] == null){
                totalVotes = 0;
            } else {
                totalVotes = Double.parseDouble(fData[i][votes].trim());
            }
            String key2 = fData[i][key2Index];
            if(fData[i][key2Index] == "" || fData[i][key2Index] == null){
                key2 = "";
            }
            String key3 = fData[i][key3Index];
            if(fData[i][key3Index] == "" || fData[i][key3Index] == null){
                key3 = "";
            }
            dataset.setValue(totalVotes, key2, key3);
        }

        return dataset;
        }


    private void createBarChart(int votes, int key2Index, int key3Index, String xAxis, String yAxis) {
        CategoryDataset dataset = createDataset(votes, key2Index, key3Index);
        JFreeChart chart  = ChartFactory.createBarChart(
                "Candidates total votes in different counties",
                xAxis,
                yAxis,
                dataset,
                PlotOrientation.VERTICAL, true, true, false
        );
        ChartPanel barChart = new ChartPanel(chart);
        barPanel.setLayout(new BorderLayout());
        barPanel.add(barChart, BorderLayout.NORTH);
    }
}
