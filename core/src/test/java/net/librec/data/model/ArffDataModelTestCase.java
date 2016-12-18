/**
 * Copyright (C) 2016 LibRec
 * 
 * This file is part of LibRec.
 * LibRec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibRec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibRec. If not, see <http://www.gnu.org/licenses/>.
 */
package net.librec.data.model;

import net.librec.BaseTestCase;
import net.librec.common.LibrecException;
import net.librec.conf.Configured;
import net.librec.data.DataModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ArffDataMode TestCase
 * {@link net.librec.data.model.ArffDataModel}
 *
 * @author SunYatong
 */
public class ArffDataModelTestCase extends BaseTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        conf.set(Configured.CONF_DFS_DATA_DIR, "../data");
        conf.set(Configured.CONF_DATA_COLUMN_FORMAT, "UIR");
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.ratio.ratio", "0.8");
        conf.set("data.spiltter.ratio", "ratingratio");
    }

    /**
     * Test the function of reading a file.
     *
     * @throws LibrecException
     */
    @Test
    public void testReadFile() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "arfftest/test.arff");

        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();

        assertEquals(6, dataModel.getItemMappingData().size());
        assertEquals(5, dataModel.getUserMappingData().size());
    }

    /**
     * Test the function of reading files from directory and its sub-directories.
     *
     * @throws LibrecException
     */
    @Test
    public void testReadDir() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "arfftest");
        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();

        assertEquals(6, dataModel.getItemMappingData().size());
        assertEquals(9, dataModel.getUserMappingData().size());
    }

    /**
     * test the function of splitter part
     * {@link net.librec.data.splitter.RatioDataSplitter} split the data by
     * rating ratio
     *
     * @throws LibrecException
     */
    @Test
    public void testRatingRatio() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "test/ratings.arff");
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.ratio.ratio", "0.8");
        conf.set("data.spiltter.ratio", "ratingratio");

        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();

        double actualRatio = getTrainRatio(dataModel);
        assertTrue(Math.abs(actualRatio - 0.8) <= 0.01);
    }

    /**
     * test the function of splitter part
     * {@link net.librec.data.splitter.RatioDataSplitter} split the data by user
     * ratio
     *
     * @throws LibrecException
     */
    @Test
    public void testUserRatio() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "test/ratings.arff");
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.ratio.ratio", "0.8");
        conf.set("data.spiltter.ratio", "userratio");

        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();

        double actualRatio = getTrainRatio(dataModel);
        assertTrue(Math.abs(actualRatio - 0.8) <= 0.01);
    }

    /**
     * test the function of splitter part
     * {@link net.librec.data.splitter.RatioDataSplitter} split the data by item
     * ratio
     *
     * @throws LibrecException
     */
    @Test
    public void testItemRatio() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "test/ratings.arff");
        conf.set("data.model.splitter", "ratio");
        conf.set("data.splitter.ratio.ratio", "0.8");
        conf.set("data.spiltter.ratio", "itemratio");

        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();

        double actualRatio = getTrainRatio(dataModel);
        assertTrue(Math.abs(actualRatio - 0.8) <= 0.01);
    }

    /**
     * test the function of splitter part
     * {@link net.librec.data.splitter.RatioDataSplitter} split the data by
     * rating ratio into 3 sets: train,test,valid.
     *
     * @throws LibrecException
     */
    @Test
    public void testValidRatio() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "test/ratings.arff");
        conf.set("data.model.splitter", "ratio");
        conf.set("data.spiltter.ratio", "validratio");
        conf.set("data.splitter.ratio.train", "0.5");
        conf.set("data.splitter.ratio.valid", "0.3");

        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();

        double actualTrainRatio = getTrainRatio(dataModel);
        assertTrue(Math.abs(actualTrainRatio - 0.5) <= 0.01);
        double actualValidRatio = getValidRatio(dataModel);
        assertTrue(Math.abs(actualValidRatio - 0.3) <= 0.05);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.KCVDataSplitter} Split all ratings for
     * k-fold cross validation.
     *
     * @throws LibrecException
     */
    @Test
    public void testKCV() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "arfftest/test.arff");
        conf.set("data.model.splitter", "net.librec.data.splitter.KCVDataSplitter");
        conf.set("data.splitter.cv.number", "5");

        for (int i = 1; i <= 5; i++) {
            conf.set("data.splitter.cv.index", i + "");

            ArffDataModel dataModel = new ArffDataModel(conf);
            dataModel.buildDataModel();
            System.out.println("index: " + i);
            assertEquals(8, getTrainSize(dataModel));
            assertEquals(2, getTestSize(dataModel));
        }
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.LOOCVDataSplitter} Each user splits out a
     * rating for test set,the rest for train set.
     *
     * @throws LibrecException
     */
    @Test
    public void testLOOByUser() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "arfftest/test.arff");
        conf.set("data.model.splitter", "net.librec.data.splitter.LOOCVDataSplitter");
        conf.set("data.splitter.loocv", "loobyuser");

        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();

        assertEquals(getTrainSize(dataModel), 5);
        assertEquals(getTestSize(dataModel), 5);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.LOOCVDataSplitter} Each item splits out a
     * rating for test set,the rest for train set.
     *
     * @throws LibrecException
     */
    @Test
    public void testLOOByItem() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "arfftest/test.arff");
        conf.set("data.model.splitter", "net.librec.data.splitter.LOOCVDataSplitter");
        conf.set("data.splitter.loocv", "loobyitem");

        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();

        assertEquals(getTrainSize(dataModel), 4);
        assertEquals(getTestSize(dataModel), 6);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.GivenNDataSplitter} Each user splits out
     * N ratings for test set,the rest for training set.
     *
     * @throws LibrecException
     */
    @Test
    public void testGivenNByUser() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "arfftest/test.arff");
        conf.set("data.model.splitter", "net.librec.data.splitter.GivenNDataSplitter");
        conf.set("data.splitter.givenn", "getgivennbyuser");
        conf.set("data.splitter.givenn.n", "1");

        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();

        assertEquals(getTrainSize(dataModel), 5);
        assertEquals(getTestSize(dataModel), 5);
    }

    /**
     * Test the function of splitter part.
     * {@link net.librec.data.splitter.GivenNDataSplitter} Each item splits out
     * N ratings for test set,the rest for training set.
     *
     * @throws LibrecException
     */
    @Test
    public void testGivenNByItem() throws LibrecException {
        conf.set(Configured.CONF_DATA_INPUT_PATH, "arfftest/test.arff");
        conf.set("data.model.splitter", "net.librec.data.splitter.GivenNDataSplitter");
        conf.set("data.splitter.givenn", "getgivennbyitem");
        conf.set("data.splitter.givenn.n", "1");

        ArffDataModel dataModel = new ArffDataModel(conf);
        dataModel.buildDataModel();

        assertEquals(getTrainSize(dataModel), 6);
        assertEquals(getTestSize(dataModel), 4);
    }

    /**
     * Returns the size of preference matrix of a specified DataModel object
     *
     * @param dataModel
     *            a DataModel object
     * @return the size of preference matrix of a specified DataModel object
     */
    public int getDataSize(DataModel dataModel) {
        int sum = 0;
        int train = getTrainSize(dataModel);
        int test = getTestSize(dataModel);
        if (null != dataModel.getDataSplitter().getValidData()) {
            int valid = getValidSize(dataModel);
            sum += valid;
        }
        sum = sum + train + test;
        return sum;
    }

    /**
     * Returns the size of training matrix of a specified DataModel object
     *
     * @param dataModel
     *            a DataModel object
     * @return the size of training matrix of a specified DataModel object
     */
    public int getTrainSize(DataModel dataModel) {
        return dataModel.getDataSplitter().getTrainData().size();
    }

    /**
     * Returns the size of test matrix of a specified DataModel object
     *
     * @param dataModel
     *            a DataModel object
     * @return the size of test matrix of a specified DataModel object
     */
    public int getTestSize(DataModel dataModel) {
        return dataModel.getDataSplitter().getTestData().size();
    }

    /**
     * Returns the size of validation matrix of a specified DataModel object
     *
     * @param dataModel
     *            a DataModel object
     * @return the size of validation matrix of a specified DataModel object
     */
    public int getValidSize(DataModel dataModel) {
        return dataModel.getDataSplitter().getValidData().size();
    }

    /**
     * calculate the ratio of training set of a specified DataModel object
     *
     * @param dataModel
     *            a DataModel object
     * @return the ratio of training set of a specified DataModel object
     */
    public double getTrainRatio(DataModel dataModel) {
        double trainSize = getTrainSize(dataModel);
        double totalSize = getDataSize(dataModel);

        return trainSize / totalSize;
    }

    /**
     * calculate the ratio of validation set of a specified DataModel object
     *
     * @param dataModel
     *            a DataModel object
     * @return the ratio of validation set of a specified DataModel object
     */
    public double getValidRatio(DataModel dataModel) {
        double validSize = getValidSize(dataModel);
        double totalSize = getDataSize(dataModel);

        return validSize / totalSize;
    }
}
