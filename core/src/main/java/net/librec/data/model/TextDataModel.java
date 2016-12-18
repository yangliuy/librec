/**
 * Copyright (C) 2016 LibRec
 * <p>
 * This file is part of LibRec.
 * LibRec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * LibRec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with LibRec. If not, see <http://www.gnu.org/licenses/>.
 */
package net.librec.data.model;

import com.google.common.collect.BiMap;
import net.librec.common.LibrecException;
import net.librec.conf.Configuration;
import net.librec.conf.Configured;
import net.librec.data.DataModel;
import net.librec.data.DataSplitter;
import net.librec.data.convertor.TextDataConvertor;
import net.librec.data.splitter.KCVDataSplitter;
import net.librec.util.DriverClassUtil;
import net.librec.util.ReflectionUtil;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * Generic Data Model
 *
 * @author WangYuFeng
 */
public class TextDataModel extends AbstractDataModel implements DataModel {

    private TextDataConvertor dataConvertor;

    public TextDataModel() {
    }

    public TextDataModel(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public void buildModel() throws LibrecException {
        String splitter = conf.get("data.model.splitter");
        String dfsDataDir = conf.get(Configured.CONF_DFS_DATA_DIR);
        String inputDataPath = dfsDataDir + "/" + conf.get(Configured.CONF_DATA_INPUT_PATH);
        String dataColumnFormat = conf.get(Configured.CONF_DATA_COLUMN_FORMAT);
        if (StringUtils.isNotBlank(dataColumnFormat)) {
            dataConvertor = new TextDataConvertor(dataColumnFormat, inputDataPath);
        } else {
            dataConvertor = new TextDataConvertor(inputDataPath);
        }
        try {
            dataConvertor.processData();
            dataSplitter = (DataSplitter) ReflectionUtil.newInstance(DriverClassUtil.getClass(splitter), conf);
        } catch (IOException e) {
            throw new LibrecException(e);
        } catch (ClassNotFoundException e) {
            throw new LibrecException(e);
        }
        if (dataSplitter != null) {
            dataSplitter.setDataConvertor(dataConvertor);
            if (dataSplitter instanceof KCVDataSplitter) {
                ((KCVDataSplitter) dataSplitter).splitFolds();
            }
            dataSplitter.splitData();
            trainDataSet = dataSplitter.getTrainData();
            testDataSet = dataSplitter.getTestData();
            LOG.info("Data size of trainning is " + trainDataSet.size());
            LOG.info("Data size of testing is " + testDataSet.size());
        }
    }

    @Override
    public void loadDataModel() throws LibrecException {

    }

    @Override
    public void saveDataModel() throws LibrecException {

    }

    @Override
    public BiMap<String, Integer> getUserMappingData() {
        return dataConvertor.getUserIds();
    }

    @Override
    public BiMap<String, Integer> getItemMappingData() {
        return dataConvertor.getItemIds();
    }

}
