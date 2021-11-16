async function renderZoneTimelineChart(element) {
  const data = await getData('zone_timeline.json')
  
  const options = {
    title: {
      text: 'Zone Timeline',
    },
    chart: {
      type: 'rangeBar',
    },
    series: data.series,
    plotOptions: {
      bar: {
        horizontal: true,
        // barHeight: '50%',
        rangeBarGroupRows: true
      }
    },
  }
  
  new ApexCharts(element, options).render();
}
