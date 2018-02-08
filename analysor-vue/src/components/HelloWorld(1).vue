<template>
  <div class="content">
    <div class="note">
      <el-alert
        title="使用注意事项"
        type="info"
        description="支持作业分析和SQL分析级别{}, 请慎重使用深度设置建议不要超过10, 否则可能会因为数据过多而导致渲染时间过长！"
        show-icon>
      </el-alert>
    </div>

    <div class="form">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="120px" class="demo-ruleForm">
        <el-form-item label="库表名称" prop="dtname">
          <el-autocomplete
            class="inline-input"
            v-model="ruleForm.dtname"
            :fetch-suggestions="querySearch"
            placeholder="请输入内容"
            :trigger-on-focus="false"
            @select="handleSelect"
          ></el-autocomplete>
        </el-form-item>
        <tr>
          <td>
            <el-form-item label="分析类型" prop="anatype">
              <el-radio-group v-model="ruleForm.anatype">
                <el-radio label="血缘分析" value="false"></el-radio>
                <el-radio label="影响分析" value="true"></el-radio>
              </el-radio-group>
            </el-form-item>
          </td>
          <td>
            <el-form-item label="分析级别" prop="analevel">
              <el-radio-group v-model="ruleForm.analevel">
                <el-radio label="作业分析"></el-radio>
                <el-radio disabled label="SQL分析"></el-radio>
              </el-radio-group>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td>
            <el-form-item label="过滤临时表" prop="isfiltertmp">
              <el-tooltip :content="''+ruleForm.isfiltertmp" placement="top">
                <el-switch
                  v-model="ruleForm.isfiltertmp"
                  active-color="#13ce66"
                  inactive-color="#475669"
                  active-value="已开启:根据tmp关键字过滤"
                  inactive-value="已关闭:根据tmp关键字过滤">
                </el-switch>
              </el-tooltip>
            </el-form-item>
          </td>
          <td>
            <el-form-item label="分析字段" prop="iscol">
              <el-tooltip :content="''+ruleForm.iscol" placement="top">
                <el-switch
                  v-model="ruleForm.iscol"
                  active-color="#13ce66"
                  inactive-color="#475669"
                  active-value="已开启:分析级别为SQL分析时有效"
                  inactive-value="已关闭:分析级别为SQL分析时有效">
                </el-switch>
              </el-tooltip>
            </el-form-item>
          </td>
        </tr>

        <el-form-item label="分析深度设置" prop="deeplevel">
          <el-slider
            v-model="ruleForm.deeplevel"
            show-input>
          </el-slider>
        </el-form-item>

        <el-form-item>
          <table border="0" cellspacing="5" cellpadding="5">
            <tr>
              <td>
                <el-button type="primary" @click="submitForm('ruleForm')">立即分析</el-button>
              </td>
              <td>
                <el-button type="primary" @click="resetForm('ruleForm')">重置</el-button>
              </td>
              <td>
                <el-popover
                  ref="popover4"
                  placement="right"
                  width="600"
                  trigger="click">

                  <el-form-item label="过滤关键词" prop="filter">
                    <el-tag
                      type="primary"
                      :key="tag"
                      v-for="tag in ruleForm.filterkeys"
                      :closable="true"
                      :close-transition="false"
                      @close="handleClose(tag)"
                    >
                      {{tag}}
                    </el-tag>
                    <el-input
                      class="input-new-tag"
                      v-if="ruleForm.inputVisible"
                      v-model="ruleForm.inputValue"
                      ref="saveTagInput"
                      size="small"
                      prefix-icon="edit"
                      @keyup.enter.native="handleInputConfirm"
                      @blur="handleInputConfirm"
                    >
                    </el-input>
                    <el-button v-else  size="small" icon="plus" type="info"
                               @click="showInput">
                    </el-button>
                  </el-form-item>
                  <el-form-item label="过滤规则">
                    <el-radio-group v-model="ruleForm.filterrule1">
                      <el-radio label="包含"></el-radio>
                      <el-radio label="排除"></el-radio>
                    </el-radio-group>
                    <el-radio-group v-model="ruleForm.filterrule2">
                      <el-radio label="精确匹配"></el-radio>
                      <el-radio label="模糊匹配"></el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="过滤范围">
                    <el-radio-group v-model="ruleForm.fiterlrange">
                      <el-radio label="作业名"></el-radio>
                      <el-radio label="表名"></el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="runfilter()">执行过滤规则</el-button>
                    <el-button type="danger" @click="dialogVisible = true">还原</el-button>
                  </el-form-item>
                </el-popover>
                <el-button  margin-left="10px" type="warning" prefix-icon="edit" v-popover:popover4>过滤器
                </el-button>
              </td>
            </tr>
          </table>
        </el-form-item>
      </el-form>

      <el-dialog
        title="温馨提示"
        :visible.sync="dialogVisible"
        size="tiny"
        :before-close="handleCloseReset">
        <span>还原后将失去所有已经执行过的过滤规则!</span>
        <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="restore">确 定</el-button>
                </span>
      </el-dialog>

    </div>

         
    <div id="main"></div>
    <div id="test" class="test">

      <codemirror
        v-model="textarea"
        :options="editorOption">
      </codemirror>

      <div v-for=" node in nodes">
        <li>{{ node }}</li>
      </div>
      <div v-for=" link in links">
        <li>{{ link }}</li>
      </div>
    </div>
  </div>
</template>
<script>

  import echarts from 'echarts'
  import $ from 'jquery'

  require('../../node_modules/echarts/chart/chord')
  require('../../node_modules/echarts/chart/force')
  export default {
    data() {
      return {
        dialogVisible: false,
        newnodes: [],
        nodefordispalys: [],
        nodes: [],
        links: [],
        nodescache: [],
        linkscache: [],
        textarea: '',
        ruleForm: {
          dtname: 'gld.TEST_TABLE2',
          analevel: '作业分析',
          iscol: false,
          isfiltertmp: '已开启:根据tmp关键字过滤',
//                    filter: [],
          anatype: '血缘分析',
          deeplevel: 3,
          filterrule1: '排除',
          filterrule2: '模糊匹配',
          filterkeys: [],
          inputVisible: false,
          inputValue: '',
          fiterlrange: '表名'
        },
        rules: {
          dtname: [
            { required: true, message: '输入库表信息', trigger: 'blur' },
            { min: 1, max: 40, message: '长度在 1 到 40 个字符', trigger: 'blur' }
          ],
          analevel: [
            { required: true, message: '请选择分析级别', trigger: 'change' }
          ],
          anatype: [
            { required: true, message: '请选择分析类型', trigger: 'change' }
          ]
        },


        editorOption: {
          lineWrapping: true, //是换行(wrap)还是滚动(scroll)，默认为滚动(scroll)
          readOnly: true,
          showCursorWhenSelecting: true,//选择时是否显示光标
          cursorHeight: 0.85,  //光标高度
          tabSize: 4,
          styleActiveLine: true,
          lineNumbers: true,  //行号
          line: true,
          mode: 'text/x-mysql',
          theme: 'mdn-like',
          styleSelectedText: true,
        }
      }
    },

    methods: {

      getAllNodes() {
        var that = this;
        $.ajax({
          url: '/lineage/getallnodes',
          dataType: 'json',
          type: 'GET',
          success: function (data) {
            that.nodefordispalys = JSON.parse(data.msg.nodes);
          }
        });
      },

      querySearch(queryString, cb) {
        var node = this.nodefordispalys;
        var results = queryString ? node.filter(this.createFilter(queryString)) : node;
        cb(results);
      },
      createFilter(queryString) {
        return (node) => {
          return (node.value.indexOf(queryString.toLowerCase()) >= 0);
        };
      },
      handleSelect(item) {
        console.log(item);
      },


      handleCloseReset(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {
          });
      },
      restore() {
        this.dialogVisible = false;
        this.nodes = this.nodescache;
        this.links = this.linkscache;

        if (eval(this.nodes).length > 0) {
          this.$notify({
            title: '执行成功',
            message: '还原后节点数量为:' + eval(this.nodes).length + ';',
            type: 'success',
            offset: 150
          });
          this.drawGraph('main');
        } else {
          this.$notify({
            title: '执行成功',
            message: '但是过滤后节点数量为:' + eval(this.nodes).length + ';',
            type: 'warning',
            offset: 150
          });
        }
      },
      runfilter() {
        var originallength = eval(this.nodes).length;
        if (eval(this.nodes).length == 0) {
          this.$notify({
            title: '错误',
            message: '没有数据可以执行过滤规则！请先点击[立即分析]按钮获取数据！',
            type: 'warning',
            offset: 1
          });
        } else {
          if (this.ruleForm.filterkeys.length == 0) {
            this.$notify({
              title: '错误规则',
              message: '请至少输入一个关键词!',
              type: 'warning',
              offset: 1
            });
          } else {
            var fiterRule = false;
            var isRuleisExactly = false;
            var category = 1;
            if (this.ruleForm.filterrule1 == '包含') {
              fiterRule = true;
            }
            ;
            if (this.ruleForm.filterrule2 == '精确匹配') {
              isRuleisExactly = true;
            }
            ;
            if (this.ruleForm.fiterlrange == '作业名') {
              category = 2;
            }
            ;

            this.dofilter(this.nodes, this.ruleForm.filterkeys, fiterRule, isRuleisExactly, category);
            if (originallength == eval(this.nodes).length) {
              this.$notify({
                title: '过滤规则执行失败',
                message: '没有发现此规则可以过滤掉的数据!',
                type: 'error',
                offset: 1
              });
            } else {
              if (eval(this.nodes).length > 0) {
                this.$notify({
                  title: '过滤规则执行成功',
                  message: '过滤后节点数量为:' + eval(this.nodes).length + ';',
                  type: 'success',
                  offset: 1
                });
                this.drawGraph('main');
              } else {
                this.$notify({
                  title: '过滤规则执行成功',
                  message: '但是过滤后节点数量为:' + eval(this.nodes).length + ';',
                  type: 'warning',
                  offset: 1
                });
              }
            }
          }
        }
      },

      dofilter(nodesin, filterKeyWords, filterRule, filterRuleisExactly, category) {
        this.newnodes.length = 0;
        for (var i in nodesin) {
          if (nodesin[i].category == 0 || nodesin[i].category == category) {
            this.newnodes.push(nodesin[i])
          } else {
            var isadd = true;
            for (var j in filterKeyWords) {
              if (filterRule) {

                if (filterRuleisExactly) {
                  if (filterKeyWords[j] == nodesin[i].name) {
                    isadd = isadd && true;
                  } else {
                    isadd = isadd && false;
                  }
                } else {
                  if (this.isContains(nodesin[i].name, filterKeyWords[j])) {
                    isadd = isadd && true;
                  } else {
                    isadd = isadd && false;
                  }
                }
              } else {//不包含 多个关键词会有问题
                if (filterRuleisExactly) {
                  if (filterKeyWords[j] != nodesin[i].name) {
                    isadd = isadd && true;
                  } else {
                    isadd = isadd && false;
                  }
                } else {
                  if (this.isNotContains(nodesin[i].name, filterKeyWords[j])) {
                    isadd = isadd && true;

                  } else {
                    isadd = isadd && false;
                  }
                }
              }
            }
            if (isadd) {
              this.newnodes.push(nodesin[i])
            }
          }
        }
        this.nodes = this.newnodes.slice(0);
//                this.ruleForm.filterkeys=[];//clear
      },
      arrayRemove(arr, val) {
        var index = arr.indexOf(val);
        if (index > -1) {
          arr.splice(index, 1);
        }
      },
      isArrayContains(arr, obj) {
        var i = arr.length;
        while (i--) {
          if (arr[i] === obj) {
            return true;
          }
        }
        return false;
      },
      isContains(str, substr) {
        return str.toLowerCase().indexOf(substr.toLowerCase()) >= 0;
      },
      isNotContains(str, substr) {
        return str.toLowerCase().indexOf(substr.toLowerCase()) < 0;
      },

      handleClose(tag) {
        this.ruleForm.filterkeys.splice(this.ruleForm.filterkeys.indexOf(tag), 1);
      },

      showInput() {
        this.ruleForm.inputVisible = true;
        this.$nextTick(_ => {
          this.$refs.saveTagInput.$refs.input.focus();
        });
      },

      handleInputConfirm() {
        let inputValue = this.ruleForm.inputValue;
        if (inputValue) {
          this.ruleForm.filterkeys.push(inputValue);
        }
        this.ruleForm.inputVisible = false;
        this.ruleForm.inputValue = '';
      },


      submitForm(formName) {

        this.$refs[formName].validate((valid) => {
          if (valid) {
            var ischild = true;
            if (this.ruleForm.anatype == '血缘分析') {
              ischild = false;
            }
            var that = this;
            $.ajax({
              url: 'lineage/search?database=' + this.ruleForm.dtname.split('.')[0] + '&table=' + this.ruleForm.dtname.split('.')[1] + '&level=job&ischild=' + ischild + '&iscols=false&deeplevel=' + this.ruleForm.deeplevel,
              dataType: 'json',
              type: 'GET',
              success: function (data) {
                that.nodescache = JSON.parse(data.msg.nodes);
                that.linkscache = JSON.parse(data.msg.links);
                that.nodes = JSON.parse(data.msg.nodes);
                that.links = JSON.parse(data.msg.links);

                that.textarea = data.msg.nodes + data.msg.links;
                if (eval(that.nodes).length > 0) {
                  that.$notify({
                    title: '执行成功',
                    message: '返回节点数量为:' + eval(that.nodes).length + ';'
                    + '; 返回边数量为:' + eval(that.links).length + ';',
                    type: 'success',
                    offset: 150
                  });
                  that.drawGraph('main');
                } else {
                  that.$notify({
                    title: '执行成功',
                    message: '但是返回节点数量为:' + eval(that.nodes).length + ';',
                    type: 'warning',
                    offset: 150
                  });
                }


                if (that.isContains(that.ruleForm.isfiltertmp, '已开启')) {
                  var filterKeyWords = ["tmp"];
                  var filterRule = false;//true 包含 false 排除
                  var filterRuleisExactly = false;//true 精确 false 模糊
                  that.dofilter(that.nodes, filterKeyWords, filterRule, filterRuleisExactly, 1);
                }

              }
            });
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },

      resetForm(formName) {
        this.$refs[formName].resetFields();
      },

      drawGraph(id) {
        var that = this;

        this.chart = echarts.init(document.getElementById(id));

        var option = {
          title: {
            text: ''
          },
          tooltip: {
            trigger: 'axis',
            formatter: '{a} : {b}'
          },
          toolbox: {
            show: true,
            feature: {
              restore: { show: true },
              magicType: { show: true, type: ['force', 'chord'] },
              saveAsImage: { show: true }
            }
          },
          legend: {
            x: 'center',
            data: ['作业', 'SQL', '表', '分析目标']
          },
          series: [
            {
              type: 'force',
              ribbonType: false,
              categories: [
                {
                  name: '分析目标',
                  symbol: 'triangle',
                  symbolSize: [32, 32],
                  draggable: true
                },
                {
                  name: '作业',
                  symbol: 'circle',
                  symbolSize: [20, 20],
                  draggable: true
                },
                {
                  name: '表',
                  symbol: 'diamond',
                  symbolSize: [20, 20],
                  draggable: true

                }
                ,
                {
                  name: 'SQL',
                  symbol: 'square',
                  symbolSize: [20, 20],
                  draggable: true

                }
              ],
              itemStyle: {
                normal: {
                  label: {
                    position: 'bottom',
                    show: true,
                    textStyle: {
                      color: 'gray',
                      size: 5
                    }
                  },
                  nodeStyle: {
                    brushType: 'both',
                    borderColor: 'rgba(255,215,0,0.4)',
                    borderWidth: 1
                  },
                  linkStyle: {
                    weight: 1,
                    color: 'lightblue'
                  }
                },
                emphasis: {
                  label: {
                    show: true,
                    textStyle: {
                      fontSize: 18,
                      fontWeight: 'bolder',
                      color: '#333'          // 主标题文字颜色
                    }
                  },
                  linkStyle: {
                    normal: { color: 'red' }
                  }
                }
              }, minRadius: 15,
              maxRadius: 25,
              gravity: 1.1,
              scaling: 1.2,
              draggable: true,
              linkSymbol: 'arrow',
              steps: 10,
              coolDown: 0.9,
              preventOverlap: true,
              nodes: this.nodes,
              links: this.links
            }
          ]
        };
        this.chart.setOption(option);
        this.chart.hideLoading();
        this.chart.on('click', function (param) {
            var data = param.data;
            var links = option.series[0].links;
            var nodes = option.series[0].nodes;
            if (
              data.source != null
              && data.target != null
            ) { //点击的是边
              var sourceNode = nodes.filter(function (n) {
                return n.name == data.source
              })[0];
              var targetNode = nodes.filter(function (n) {
                return n.name == data.target
              })[0];
              console.log("选中了边 " + sourceNode.name + ' -> ' + targetNode.name + ' (' + data.weight + ')');
            } else { // 点击的是点
              if (param.data.category == 1) {  // 作业任务
              }
              if (param.data.category == 2) {  // Hql表
                alert("dbName:" + data.name.split('.')[0] + "  tableName:" + data.name.split('.')[1]);
              }
            }
          }
        );
        this.chart.on('mouseover', function () {
          console.log(this.chart.chart.force.getPosition());
        });
      }
    }
    ,
    mounted() {
      this.getAllNodes();
    }
  }
</script>


<style scoped>
  .content {
  }

  #main {
    height: 1000px;
    width: 90%;
  }

  .form {
    width: 800px;
    align-content: left;
  }

  .note {
    width: 80%;
    margin: auto;
    margin-bottom: 10px;

  }

  .test {
    margin-bottom: 100px;
    border: 1px solid #4e4e4eee;
  }

  .inline-input {
    width: 90%;
  }

</style>
