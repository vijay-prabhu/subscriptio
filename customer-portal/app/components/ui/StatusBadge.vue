<script setup lang="ts">
const props = defineProps<{ status: string }>()

const variantMap: Record<string, string> = {
  ACTIVE: 'bg-emerald-900/50 text-emerald-400 border-emerald-800',
  TRIALING: 'bg-blue-900/50 text-blue-400 border-blue-800',
  PAST_DUE: 'bg-amber-900/50 text-amber-400 border-amber-800',
  CANCELLED: 'bg-red-900/50 text-red-400 border-red-800',
  EXPIRED: 'bg-zinc-800 text-zinc-400 border-zinc-700',
  PAID: 'bg-emerald-900/50 text-emerald-400 border-emerald-800',
  OPEN: 'bg-blue-900/50 text-blue-400 border-blue-800',
  DRAFT: 'bg-zinc-800 text-zinc-400 border-zinc-700',
}

const classes = computed(() => variantMap[props.status] || 'bg-zinc-800 text-zinc-400 border-zinc-700')
</script>

<template>
  <span :class="['inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border', classes]">
    {{ status }}
  </span>
</template>
