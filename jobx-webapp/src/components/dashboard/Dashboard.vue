<template>
  <main class="main">
    <section class="content">
      <header class="content__title">
        <h1>Dashboard</h1>
        <small>Welcome to the unique SuperAdmin web app experience!</small>
      </header>

      <div class="row quick-stats">
        <div class="col-sm-6 col-md-3" v-for="item in quickstats">
          <div class="quick-stats__item">
            <div class="quick-stats__info">
              <h2>{{item.number}}</h2>
              <small>{{item.desc}}</small>
            </div>
            <div class="quick-stats__chart peity-bar">
              <i class="zmdi" :class="item.icon"></i>
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-lg-6">
          <div class="card">
            <div class="card-body">
              <h4 class="card-title">Sales Statistics</h4>
              <div class="flot-chart flot-curved-line" ref="flotCurved"></div>
              <div class="flot-chart-legends flot-chart-legends--curved" ref="flotCurvedLegends"></div>
            </div>
          </div>
        </div>

        <div class="col-lg-6">
          <div class="card">
            <div class="card-body">
              <h4 class="card-title">Growth Rate</h4>
              <div class="flot-chart flot-line" ref="flotLine"></div>
              <div class="flot-chart-legends flot-chart-legends--line" ref="flotLineLegends"></div>
            </div>
          </div>
        </div>
      </div>

      <Footer></Footer>

    </section>
  </main>
</template>

<script type="text/ecmascript-6">
  import 'jquery.flot'
  import 'jquery-flot-resize'
  import 'flot.curvedlines'
  import Footer from "@/components/common/Footer"

  export default {
    components: {
      Footer
    },
    data() {
      return {
        quickstats: [
          {
            number: '987,459',
            desc: 'Total Leads Recieved',
            icon: 'zmdi-laptop-chromebook'
          },
          {
            number: '356,785K',
            desc: 'Total Website Clicks',
            icon: 'zmdi-view-list'
          },
          {
            number: '$58,778',
            desc: 'Total Sales Orders',
            icon: 'zmdi-check'
          },
          {
            number: '214',
            desc: 'Total Support Tickets',
            icon: 'zmdi-close'
          }

        ]
      }
    },
    mounted() {
      this.createCurvedChart();
      this.createLineChart();
    },
    methods: {
      createCurvedChart() {
        let curvedLineChartData = [
          {
            label: "2016",
            color: "rgba(255,255,255,0.08)",
            lines: {
              show: true,
              lineWidth: 0,
              fill: 1,
              fillColor: {
                colors: ["rgba(255,255,255,0.0)", "rgba(255,255,255,0.1)"]
              }
            },
            data: [
              [10, 90],
              [20, 40],
              [30, 80],
              [40, 20],
              [50, 90],
              [60, 20],
              [70, 60]
            ]
          },
          {
            label: "2017",
            color: "rgba(255,255,255,0.8)",
            lines: {
              show: true,
              lineWidth: 0.1,
              fill: 1,
              fillColor: {
                colors: ["rgba(255,255,255,0.01)", "#fff"]
              }
            },
            data: [
              [10, 80],
              [20, 30],
              [30, 70],
              [40, 10],
              [50, 80],
              [60, 10],
              [70, 50]
            ]
          }
        ];
        // Chart Options
        // Main
        let curvedLineChartOptions = {
          series: {
            shadowSize: 0,
            curvedLines: {
              apply: true,
              active: true,
              monotonicFit: true
            },
            points: {
              show: false
            }
          },
          grid: {
            borderWidth: 1,
            borderColor: "rgba(255,255,255,0.1)",
            show: true,
            hoverable: true,
            clickable: true
          },
          xaxis: {
            tickColor: "rgba(255,255,255,0.1)",
            tickDecimals: 0,
            font: {
              lineHeight: 13,
              style: "normal",
              color: "rgba(255,255,255,0.75)",
              size: 11
            }
          },
          yaxis: {
            tickColor: "rgba(255,255,255,0.1)",
            font: {
              lineHeight: 13,
              style: "normal",
              color: "rgba(255,255,255,0.75)",
              size: 11
            },
            min: +5
          },
          legend: {
            container: this.$refs.flotCurvedLegends,
            backgroundOpacity: 0.5,
            noColumns: 0,
            lineWidth: 0,
            labelBoxBorderColor: "rgba(255,255,255,0)"
          }
        }

        $.plot(
          this.$refs.flotCurved,
          curvedLineChartData,
          curvedLineChartOptions
        )
      },

      createLineChart() {
        let lineChartData = [
          {
            label: "2015",
            data: [
              [1, 60],
              [2, 30],
              [3, 50],
              [4, 100],
              [5, 10],
              [6, 90],
              [7, 85]
            ],
            color: "#fff"
          },
          {
            label: "2016",
            data: [
              [1, 20],
              [2, 90],
              [3, 60],
              [4, 40],
              [5, 100],
              [6, 25],
              [7, 65]
            ],
            color: "rgba(255,255,255,0.5)"
          },
          {
            label: "2017",
            data: [[1, 100], [2, 20], [3, 60], [4, 90], [5, 80], [6, 10], [7, 5]],
            color: "rgba(255,255,255,0.15)"
          }
        ]

        // Chart Options
        let lineChartOptions = {
          series: {
            lines: {
              show: true,
              barWidth: 0.05,
              fill: 0
            }
          },
          shadowSize: 0.1,
          grid: {
            borderWidth: 1,
            borderColor: "rgba(255,255,255,0.1)",
            show: true,
            hoverable: true,
            clickable: true
          },
          yaxis: {
            tickColor: "rgba(255,255,255,0.1)",
            tickDecimals: 0,
            font: {
              lineHeight: 13,
              style: "normal",
              color: "rgba(255,255,255,0.75)",
              size: 11
            },
            shadowSize: 0
          },

          xaxis: {
            tickColor: "rgba(255,255,255,0.1)",
            tickDecimals: 0,
            font: {
              lineHeight: 13,
              style: "normal",
              color: "rgba(255,255,255,0.75)",
              size: 11
            },
            shadowSize: 0
          },
          legend: {
            container: this.$refs.flotLineLegends,
            backgroundOpacity: 0.5,
            noColumns: 0,
            lineWidth: 0,
            labelBoxBorderColor: "rgba(255,255,255,0)"
          }
        }
        $.plot(this.$refs.flotLine, lineChartData, lineChartOptions)
      }
    }
  }
</script>

<style lang="scss" scoped>
  .quick-stats__item {
    display:flex;
    .quick-stats__info {
      flex:1;
    }
  }
  .quick-stats__chart {
    position:static;
    float:right;
    padding:0px 10px;
    .zmdi {
      font-size:50px;
    }
  }
</style>
