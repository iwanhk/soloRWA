<template>
  <div 
    class="audit-card" 
    :style="{ background: iconBg }"
    @click="handleClick"
  >
    <div class="audit-content">
      <span class="audit-title">{{ title }}</span>
      <span class="audit-count">{{ count }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'AuditCard' })

interface Props {
  title: string
  count: number
  icon?: string // Keep prop to avoid breaking parent usage, even if unused
  iconBg?: string
}

const props = withDefaults(defineProps<Props>(), {
  count: 0,
  iconBg: '#409eff'
})

const emit = defineEmits<{
  click: []
}>()

const handleClick = () => {
  emit('click')
}
</script>

<style scoped>
.audit-card {
  cursor: pointer;
  transition: all 0.3s;
  padding: 12px 24px;
  border-radius: 8px;
  color: #fff;
  display: inline-block;
  min-width: 160px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.audit-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
  opacity: 0.95;
}

.audit-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
}

.audit-count {
  font-weight: 700;
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 14px;
}
</style>

